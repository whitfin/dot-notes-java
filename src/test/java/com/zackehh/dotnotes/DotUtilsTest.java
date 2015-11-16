package com.zackehh.dotnotes;

import junit.framework.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class DotUtilsTest {

    @Test(expectedExceptions = InvocationTargetException.class)
    public void creationOfDotUtilsDisabled() throws Exception {
        Constructor<DotUtils> constructor = DotUtils.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
