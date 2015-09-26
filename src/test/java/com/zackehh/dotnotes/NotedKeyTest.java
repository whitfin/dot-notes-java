package com.zackehh.dotnotes;

import com.zackehh.dotnotes.util.NotedKey;
import junit.framework.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class NotedKeyTest {

    @Test
    public void testKeyCreationUsingNew() throws Exception {
        NotedKey strKey = getCtor(String.class).newInstance("test");

        Assert.assertNotNull(strKey);
        Assert.assertTrue(strKey.isString());
        Assert.assertFalse(strKey.isNumber());
        Assert.assertNull(strKey.asNumber());
        Assert.assertNotNull(strKey.asString());
        Assert.assertEquals(strKey.asString(), "test");

        NotedKey numKey = getCtor(Integer.class).newInstance(5);

        Assert.assertNotNull(numKey);
        Assert.assertFalse(numKey.isString());
        Assert.assertTrue(numKey.isNumber());
        Assert.assertNotNull(numKey.asNumber());
        Assert.assertNull(numKey.asString());
        Assert.assertEquals((int) numKey.asNumber(), 5);
    }

    @Test
    public void testKeyCreationUsingOf() throws Exception {
        NotedKey strKey = NotedKey.of("test");

        Assert.assertNotNull(strKey);
        Assert.assertTrue(strKey.isString());
        Assert.assertFalse(strKey.isNumber());
        Assert.assertNull(strKey.asNumber());
        Assert.assertNotNull(strKey.asString());
        Assert.assertEquals(strKey.asString(), "test");

        NotedKey numKey = NotedKey.of(5);

        Assert.assertNotNull(numKey);
        Assert.assertFalse(numKey.isString());
        Assert.assertTrue(numKey.isNumber());
        Assert.assertNotNull(numKey.asNumber());
        Assert.assertNull(numKey.asString());
        Assert.assertEquals((int) numKey.asNumber(), 5);

        Assert.assertNull(NotedKey.of(Boolean.TRUE));
    }

    private <T> Constructor<NotedKey> getCtor(Class<T> tClass) throws NoSuchMethodException {
        Constructor<NotedKey> ctor = NotedKey.class.getDeclaredConstructor(tClass);
        ctor.setAccessible(true);
        Assert.assertTrue(Modifier.isPrivate(ctor.getModifiers()));
        return ctor;
    }

}
