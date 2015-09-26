package com.zackehh.dotnotes;

import com.zackehh.dotnotes.util.NotedKey;
import junit.framework.Assert;
import org.testng.annotations.Test;

public class NotedKeyTest {

    @Test
    public void testKeyCreationUsingNew(){
        NotedKey strKey = new NotedKey("test");

        Assert.assertNotNull(strKey);
        Assert.assertTrue(strKey.isString());
        Assert.assertFalse(strKey.isNumber());
        Assert.assertNull(strKey.asNumber());
        Assert.assertNotNull(strKey.asString());
        Assert.assertEquals(strKey.asString(), "test");

        NotedKey numKey = new NotedKey(5);

        Assert.assertNotNull(numKey);
        Assert.assertFalse(numKey.isString());
        Assert.assertTrue(numKey.isNumber());
        Assert.assertNotNull(numKey.asNumber());
        Assert.assertNull(numKey.asString());
        Assert.assertEquals((int) numKey.asNumber(), 5);
    }

    @Test
    public void testKeyCreationUsingOf(){
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
    }

}
