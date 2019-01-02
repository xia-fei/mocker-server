package org.wing.mocker.core.handler;


import org.wing.mocker.core.AbstractGenerateHandler;
import org.wing.mocker.core.MockSettings;

import java.util.Random;

public class StringGenerateHandler extends AbstractGenerateHandler {
    private  final MockSettings mockSettings;
    public StringGenerateHandler(MockSettings mockSettings) {
        this.mockSettings=mockSettings;
    }

    @Override
    public boolean canHandler(Class clazz) {
        return String.class.isAssignableFrom(clazz);
    }

    @Override
    public  Object defaultValue(Class clazz) {
        return generateRandomString();
    }

    @Override
    public  Object mockInstance(String str, Class mockClass) {
        return str;
    }

    private String generateRandomString() {
        String[] randomString = mockSettings.getRandomStringSource().split(",");
        return randomString[new Random().nextInt(randomString.length)];
    }

}
