package org.wing.mocker.core.test;

import org.junit.Assert;
import org.junit.Test;
import org.wing.mocker.core.TypeHelper;

import java.util.HashMap;
import java.util.Map;

public class TypeHelperTest {




    @Test
    public void testArray() {
        Assert.assertTrue(new TypeHelper(Object[].class).isObjectArray());
        Assert.assertTrue(new TypeHelper(String[].class).isObjectArray());
        Assert.assertFalse(new TypeHelper(byte[].class).isObjectArray());
        Assert.assertFalse(new TypeHelper(int[].class).isObjectArray());
        Assert.assertFalse(new TypeHelper(long[].class).isObjectArray());
    }

    @Test
    public void testMap() throws NoSuchMethodException {
        Assert.assertTrue(new TypeHelper(TypeClassDemo.class.getDeclaredMethod("map").getGenericReturnType()).isMap());
        Assert.assertTrue(new TypeHelper(TypeClassDemo.class.getDeclaredMethod("hashMap").getGenericReturnType()).isMap());
    }

    @Test
    public void testCollection() throws NoSuchMethodException {
        Assert.assertTrue(new TypeHelper(TypeClassDemo.class.getDeclaredMethod("map").getGenericReturnType()).isMap());
        Assert.assertTrue(new TypeHelper(TypeClassDemo.class.getDeclaredMethod("hashMap").getGenericReturnType()).isMap());
    }

    class TypeClassDemo {

        Map<String, Integer> map() {
            return null;
        }
        HashMap<String, Integer> hashMap() {
            return null;
        }
    }

}

