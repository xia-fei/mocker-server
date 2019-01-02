package org.wing.mocker.core.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wing.mocker.core.AbstractGenerateHandler;

import java.util.Date;

public class DateGenerateHandler extends AbstractGenerateHandler {
    private Logger LOGGER = LoggerFactory.getLogger(DateGenerateHandler.class);

    @Override
    public boolean canHandler(Class clazz) {
        return Date.class.isAssignableFrom(clazz) || java.sql.Date.class.isAssignableFrom(clazz);
    }

    @Override
    public Object mockInstance(String str, Class mockClass) {
        LOGGER.warn("暂不支持时间类型自定义{}", mockClass);
        return null;
    }

    @Override
    public Object defaultValue(Class clazz) {
        if (Date.class == clazz) {
            return new Date();
        } else if (java.sql.Date.class == clazz) {
            return new java.sql.Date(System.currentTimeMillis());
        } else if (java.sql.Timestamp.class == clazz) {
            return new java.sql.Timestamp(System.currentTimeMillis());
        }
        throw new RuntimeException("不支持的时间类型");
    }

}
