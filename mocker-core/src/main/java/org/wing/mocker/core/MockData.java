package org.wing.mocker.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

public class MockData {

    private final static Logger LOGGER = LoggerFactory.getLogger(MockData.class);
    private final DataGenerateFactory DATA_GENERATE_FACTORY;


    private MockSettings mockSettings = new MockSettings("茕茕孑立,沆瀣一气,踽踽独行,醍醐灌顶,绵绵瓜瓞,奉为圭臬,龙行龘龘,犄角旮旯,娉婷袅娜,涕泗滂沱,呶呶不休,不稂不莠", 10, 3);


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
        ObjectPath root = new ObjectPath(null, (Class) type);
        return createAllObject(type, null, root);
    }


    private Object createAllObject(Type type, Field field, ObjectPath objectPath) {
        if (isOverDepth(objectPath, type)) {
            return null;
        }
        if (type instanceof Class) {
            Class typeClass = (Class) type;
            if (isArrayClass(typeClass)) {
                Class arrayClass;
                try {
                    arrayClass = getArrayClass(typeClass);
                } catch (ClassNotFoundException e) {
                    LOGGER.warn("数组对象Class没找到{}", typeClass, e);
                    return null;
                }
                return batchCreate(arrayClass, mockSettings.getListSize(), field, objectPath);

            }
            //如果是基础对象，则生成实例，不是则继续递归
            Object mockValue = DATA_GENERATE_FACTORY.generateValue(typeClass, field);
            if (mockValue != null) {
                return mockValue;
            } else {
                return createStandardObject(typeClass, objectPath);
            }
        } else if (isCollection(type)) {
            return createCollectionObject((ParameterizedType) type, field, objectPath);
        }
        throw new IllegalArgumentException("未能转换的类型:" + String.valueOf(type));
    }

    private Object batchCreate(Class clazz, int size, Field field, ObjectPath objectPath) {
        Object array = Array.newInstance(clazz, size);
        for (int i = 0; i < size; i++) {
            Array.set(array, i, createAllObject(clazz, field, objectPath));
        }
        return array;
    }

    private boolean isArrayClass(Class clazz) {
        String className = clazz.getName();
        return className.contains("[L") && className.contains(";");
    }


    private Class getArrayClass(Class clazz) throws ClassNotFoundException {
        String className = clazz.getName();
        className = className.substring(2, className.length() - 1);
        ClassLoader loader;
        if(clazz.getClassLoader()!=null){
            loader=clazz.getClassLoader();
        }else {
            loader=Thread.currentThread().getContextClassLoader();
        }
        return loader.loadClass(className);
    }

    private boolean isCollection(Type type) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        return Collection.class.isAssignableFrom((Class<?>) parameterizedType.getRawType());
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
    private Object createCollectionObject(ParameterizedType parameterizedType, Field field, ObjectPath objectPath) {
        if (parameterizedType.getRawType() instanceof Class) {
            Class collectionClass = (Class) parameterizedType.getRawType();
            Collection collection = (Collection) createCollectionInstance(collectionClass);
            for (int i = 0; i < mockSettings.getListSize(); i++) {
                collection.add(createAllObject(parameterizedType.getActualTypeArguments()[0], field, objectPath));
            }
            return collection;
        }
        throw new RuntimeException("类型不支持");


    }

    private Object createCollectionInstance(Class collectionClass) {
        if (collectionClass.isInterface()) {
            if (List.class.isAssignableFrom(collectionClass)) {
                return new ArrayList<>();
            } else if (Set.class.isAssignableFrom(collectionClass)) {
                return new HashSet<>();
            }
            throw new RuntimeException("不支持的集合类型" + collectionClass.toString());
        } else {
            return collectionClass.isInterface();
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
                    LOGGER.warn("字段没找到,请检查字段命名是否规范  field:{} - class:{}", propertyName, clazz.toString());
                    continue;
                }
                //产生新的节点
                ObjectPath currentPath = new ObjectPath(objectPath, propertyDescriptor.getWriteMethod().getParameterTypes()[0]);
                Object instanceValue = createAllObject(propertyDescriptor.getReadMethod().getGenericReturnType(), propertyField, currentPath);
                try {
//                    propertyDescriptor.getWriteMethod().invoke(mappedObject,instanceValue);
                    propertyField.setAccessible(true);
                    propertyField.set(mappedObject,instanceValue);
                } catch (RuntimeException|IllegalAccessException e) {
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

