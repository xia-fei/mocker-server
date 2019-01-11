package org.wing.mocker.core.handler;


import org.wing.mocker.core.AbstractGenerateHandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static java.lang.Math.abs;

public class NumberGenerateHandler extends AbstractGenerateHandler {


    private final Set<Class<?>> BASIC_NUMBER_TYPES;

    public NumberGenerateHandler() {
        Set<Class<?>> numberTypes = new HashSet<Class<?>>(19);
        numberTypes.add(Byte.class);
        numberTypes.add(Short.class);
        numberTypes.add(Integer.class);
        numberTypes.add(Long.class);
        numberTypes.add(BigInteger.class);
        numberTypes.add(Float.class);
        numberTypes.add(Double.class);
        numberTypes.add(BigDecimal.class);
        numberTypes.add(int.class);
        numberTypes.add(long.class);
        numberTypes.add(float.class);
        numberTypes.add(double.class);
        numberTypes.add(short.class);
        numberTypes.add(byte.class);
        BASIC_NUMBER_TYPES = Collections.unmodifiableSet(numberTypes);
    }

    @Override
    public boolean canHandler(Class clazz) {
        return BASIC_NUMBER_TYPES.contains(clazz);
    }

    @Override
    public Object defaultValue(Class targetClass) {
        Random random = new Random();
        if (equalsWarpTypeOrBasicType(targetClass, Byte.class)) {
            byte[] bytes = new byte[1];
            random.nextBytes(bytes);
            return (byte)abs(bytes[0]);
        } else if (equalsWarpTypeOrBasicType(targetClass, Short.class)) {
            return (short) abs(random.nextInt(Short.MAX_VALUE));
        } else if (equalsWarpTypeOrBasicType(targetClass, Integer.class)) {
            return abs(random.nextInt());
        } else if (equalsWarpTypeOrBasicType(targetClass, Long.class)) {
            return abs(random.nextLong());
        } else if (equalsWarpTypeOrBasicType(targetClass, Float.class)) {
            return abs(random.nextFloat());
        } else if (equalsWarpTypeOrBasicType(targetClass, Double.class)) {
            return abs(random.nextDouble());
        } else if (equalsWarpTypeOrBasicType(targetClass, BigInteger.class)) {
            return new BigInteger(String.valueOf(abs(random.nextInt())));
        } else if (BigDecimal.class == targetClass || Number.class == targetClass) {
            return new BigDecimal(abs(random.nextDouble()));
        } else {
            throw new IllegalArgumentException(
                    "Cannot random to target class [" + targetClass.getName() + "]");
        }
    }

    @Override
    public Object mockInstance(String str, Class mockClass) {
        return parseNumber(str, mockClass);
    }



    private Object parseNumber(String text, Class targetClass) {
        String trimmed = text.trim();
        if (equalsWarpTypeOrBasicType(targetClass, Byte.class)) {
            return Byte.valueOf(trimmed);
        } else if (equalsWarpTypeOrBasicType(targetClass, Short.class)) {
            return Short.valueOf(trimmed);
        } else if (equalsWarpTypeOrBasicType(targetClass, Integer.class)) {
            return Integer.valueOf(trimmed);
        } else if (equalsWarpTypeOrBasicType(targetClass, Long.class)) {
            return Long.valueOf(trimmed);
        } else if (equalsWarpTypeOrBasicType(targetClass, BigInteger.class)) {
            return new BigInteger(trimmed);
        } else if (equalsWarpTypeOrBasicType(targetClass, Float.class)) {
            return Float.valueOf(trimmed);
        } else if (equalsWarpTypeOrBasicType(targetClass, Double.class)) {
            return Double.valueOf(trimmed);
        } else if (BigDecimal.class == targetClass || Number.class == targetClass) {
            return new BigDecimal(trimmed);
        } else {
            throw new IllegalArgumentException(
                    "Cannot convert String [" + text + "] to target class [" + targetClass.getName() + "]");
        }
    }

    private boolean equalsWarpTypeOrBasicType(Class targetClass, Class jdkType) {
        try {
            return targetClass == jdkType || targetClass == jdkType.getDeclaredField("TYPE").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
