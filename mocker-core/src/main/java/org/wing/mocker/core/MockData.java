package org.wing.mocker.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class MockData {

    private final static Logger LOGGER = LoggerFactory.getLogger(MockData.class);
    private final DataGenerateFactory DATA_GENERATE_FACTORY;


    private MockSettings mockSettings = new MockSettings("茕茕孑立,沆瀣一气,踽踽独行,醍醐灌顶,绵绵瓜瓞,奉为圭臬,龙行龘龘,犄角旮旯,娉婷袅娜,涕泗滂沱,呶呶不休,不稂不莠", 10, 3);

    private ClassLoader externalClassLoader;

    public MockData() {
        DATA_GENERATE_FACTORY = new DataGenerateFactory(mockSettings);
    }

    public MockSettings getMockSettings() {
        return mockSettings;
    }

    /**
     * mock入口
     */
    public Object mock(Type type) {
        externalClassLoader = getTypeClassLoader(type);
        ObjectPath root = new ObjectPath(null, (Class) type);
        return createAllObject(type, null, root);
    }

    private ClassLoader getTypeClassLoader(Type type) {
        if (type instanceof Class) {
            return ((Class) type).getClassLoader();
        } else {
            return Thread.currentThread().getContextClassLoader();
        }
    }

    private Object createAllObject(Type type, Field field, ObjectPath objectPath) {
        if (isOverDepth(objectPath, type)) {
            return null;
        }
        TypeHelper typeHelper = new TypeHelper(type);

        //基础类型
        if (type instanceof Class) {
            Class typeClass = (Class) type;
            //如果是基础对象，则生成实例，不是则继续递归
            Object mockValue = DATA_GENERATE_FACTORY.generateValue(typeClass, field);
            if (mockValue != null) {
                return mockValue;
            } else {
                return createStandardObject(typeClass, objectPath);
            }
            //对象数组
        } else if (typeHelper.isObjectArray()) {
            return createObjectArray(type, objectPath);
            //集合类型
        } else if (typeHelper.isCollectionArray()) {
            return createObjectCollection(type, objectPath);
            //Map类型
        } else if (typeHelper.isMap()) {
            return createMapObject(type, objectPath);
        }
        LOGGER.warn("未能转换的类型:{},{}",type, field);
        return null;
    }


    private Object createObjectArray(Type type, ObjectPath objectPath) {
        try {
            Class arrayClass = externalClassLoader.loadClass(new TypeHelper(type).getArrayClassName());
            return createArrayInstance(arrayClass, mockSettings.getListSize(), objectPath);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @SuppressWarnings("unchecked")
    private Object createMapObject(Type type, ObjectPath objectPath) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Class keyClass = (Class) parameterizedType.getActualTypeArguments()[0];
        Class valueClass = (Class) parameterizedType.getActualTypeArguments()[1];
        Map map = new HashMap();
        for (int i = 0; i < this.mockSettings.getListSize(); i++) {
            map.put(createAllObject(keyClass, null, objectPath), createAllObject(valueClass, null, objectPath));
        }
        return map;
    }

    private Object createArrayInstance(Class clazz, int size, ObjectPath objectPath) {
        Object array = Array.newInstance(clazz, size);
        for (int i = 0; i < size; i++) {
            Array.set(array, i, createAllObject(clazz, null, objectPath));
        }
        return array;
    }


    private Class getGenericClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class) parameterizedType.getRawType();
        }
    }

    /**
     * 判断递归调用
     */
    private boolean isOverDepth(ObjectPath objectPath, Type type) {
        Class mockClass = getGenericClass(type);
        ObjectPath nextNode = new ObjectPath(objectPath, mockClass);
        nextNode.prev = objectPath;
        Map<String, Integer> counter = new HashMap<>();
        ObjectPath currentNode = nextNode;
        while (currentNode != null) {
            String className = currentNode.item.getName();
            Integer count = counter.get(className);
            if (count == null) {
                count = 0;
            }
            if (count > mockSettings.getDepth()) {
                return true;
            }
            counter.put(className, count + 1);
            currentNode = currentNode.prev;
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    private Object createObjectCollection(Type type, ObjectPath objectPath) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Collection collection = (Collection) createEmptyCollection((Class) parameterizedType.getRawType());
        for (int i = 0; i < mockSettings.getListSize(); i++) {
            collection.add(createAllObject(parameterizedType.getActualTypeArguments()[0], null, objectPath));
        }
        return collection;
    }

    private Object createEmptyCollection(Class collectionClass) {
        if (List.class.isAssignableFrom(collectionClass)) {
            return new ArrayList<>();
        } else if (Set.class.isAssignableFrom(collectionClass)) {
            return new HashSet<>();
        } else {
            throw new IllegalArgumentException("不支持的集合类" + collectionClass);
        }

    }


    /**
     * 遍历对象
     * 用get set方法生成对象
     */
    private Object createStandardObject(Class clazz, ObjectPath objectPath) {
        Object mappedObject = BeanUtils.instantiate(clazz);
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getWriteMethod() != null && propertyDescriptor.getReadMethod() != null) {
                String propertyName = propertyDescriptor.getName();
                Field propertyField = getFieldIncludeSuper(clazz, propertyName);
                if (propertyField == null) {
//                    LOGGER.debug("字段没找到,请检查字段命名是否规范  field:{} - class:{}", propertyName, clazz.toString());
                    continue;
                }
                //产生新的节点
                ObjectPath currentPath = new ObjectPath(objectPath, propertyDescriptor.getWriteMethod().getParameterTypes()[0]);
                try {
                    Object instanceValue = createAllObject(propertyDescriptor.getReadMethod().getGenericReturnType(), propertyField, currentPath);
                    propertyField.setAccessible(true);
                    propertyField.set(mappedObject, instanceValue);
                } catch (RuntimeException | IllegalAccessException e) {
                    LOGGER.error("字段设置值异常 fieldName:{},class:{}", propertyName, clazz.toString(), e);
                }

            }
        }
        return mappedObject;
    }

    private Field getFieldIncludeSuper(Class clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class parentClass = clazz.getSuperclass();
            if (parentClass != Object.class) {
                return getFieldIncludeSuper(parentClass, fieldName);
            }
        }

        return null;
    }


    /**
     * 遍历路径单向链表
     */
    private class ObjectPath {
        Class item;
        MockData.ObjectPath prev;

        ObjectPath(MockData.ObjectPath prev, Class element) {
            this.item = element;
            this.prev = prev;
        }
    }
}

