package org.wing.mocker.core.handler;

import org.wing.mocker.core.AbstractGenerateHandler;

import java.util.Random;

public class EnumGenerateHandler extends AbstractGenerateHandler {

    @Override
    public boolean canHandler(Class mockClass) {
        return Enum.class.isAssignableFrom(mockClass);
    }

    @Override
    public Object defaultValue(Class clazz) {
        Object[] enumConstants = clazz.getEnumConstants();
        return enumConstants[new Random().nextInt(enumConstants.length)];
    }

    @Override
    public Object mockInstance(String str, Class mockClass) {
        throw new RuntimeException("暂时不支持 Enum自定义值");
    }


}
