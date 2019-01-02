package org.wing.mocker.core.handler;


import org.wing.mocker.core.AbstractGenerateHandler;

import java.util.Random;

public class BooleanGenerateHandler extends AbstractGenerateHandler {

    @Override
    public boolean canHandler(Class clazz) {
        return Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz);
    }

    @Override
    public Object mockInstance(String str, Class mockClass) {
        return Boolean.valueOf(str);
    }

    @Override
    public Object defaultValue(Class clazz) {
        return new Random().nextBoolean();
    }


}
