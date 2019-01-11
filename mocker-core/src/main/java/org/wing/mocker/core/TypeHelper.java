package org.wing.mocker.core;

import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TypeHelper {
    private ParameterizedType parameterizedType;
    private Class typeClass;
    private Type type;


    public Class getTypeClass() {
        return typeClass;
    }

    public Class getRawTypeClass() {
        Assert.notNull(parameterizedType, type.toString());
        return (Class) parameterizedType.getRawType();
    }

    public Class getActualClass() {
        Assert.notNull(parameterizedType, type.toString());
        return (Class) parameterizedType.getActualTypeArguments()[0];
    }

    public String getArrayClassName() {
        Assert.notNull(typeClass, "不是数组对象，不应该调用此方法");
        String className = typeClass.getName();
        return className.substring(2, className.length() - 1);

    }

    public TypeHelper(Type type) {
        this.type = type;
        if (type instanceof ParameterizedType) {
            parameterizedType = (ParameterizedType) type;
        } else if (type instanceof Class) {
            typeClass = (Class) type;
        } else {
            throw new IllegalArgumentException("不支持的对象类型" + type.toString());
        }
    }


    /**
     * JDK基础数组类型
     */
    public boolean isObjectArray() {
        if (typeClass != null) {
            String className = typeClass.getName();
            return className.startsWith("[L") && className.endsWith(";");
        }
        return false;
    }

    public boolean isMap() {
        return parameterizedType != null && Map.class.isAssignableFrom((Class<?>) parameterizedType.getRawType());
    }

    /**
     * JDK集合类型 List,Set
     */
    public boolean isCollectionArray() {
        if (parameterizedType != null) {
            Class rawClass = (Class) parameterizedType.getRawType();
            if (List.class.isAssignableFrom(rawClass) || Set.class.isAssignableFrom(rawClass)) {
                if (parameterizedType.getActualTypeArguments().length == 1) {
                    return parameterizedType.getActualTypeArguments()[0] instanceof Class;
                }
            }
        }
        return false;
    }

}
