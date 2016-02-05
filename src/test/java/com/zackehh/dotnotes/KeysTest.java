package com.zackehh.dotnotes;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class KeysTest {

    @Test
    public void translatesBasicKey() throws Exception {
        List<NotedKey> keys = DotNotes.keys("test");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "test");
    }

    @Test
    public void translatesBasicNestedKey() throws Exception {
        List<NotedKey> keys = DotNotes.keys("test.test");

        assertNotNull(keys);
        assertEquals(keys.size(), 2);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "test");

        assertNotNull(keys.get(1));
        assertTrue(keys.get(1).isString());
        assertEquals(keys.get(1).asString(), "test");
    }

    @Test
    public void translatesArrayKey() throws Exception {
        List<NotedKey> keys = DotNotes.keys("[0]");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isNumber());
        assertEquals((int) keys.get(0).asNumber(), 0);
    }

    @Test
    public void translatesNestedArrayKey() throws Exception {
        List<NotedKey> keys = DotNotes.keys("[0][0]");

        assertNotNull(keys);
        assertEquals(keys.size(), 2);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isNumber());
        assertEquals((int) keys.get(0).asNumber(), 0);

        assertNotNull(keys.get(1));
        assertTrue(keys.get(1).isNumber());
        assertEquals((int) keys.get(1).asNumber(), 0);
    }

    @Test
    public void translatesBasicKeyUnderAnArrayKey() throws Exception {
        List<NotedKey> keys = DotNotes.keys("[0].test");

        assertNotNull(keys);
        assertEquals(keys.size(), 2);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isNumber());
        assertEquals((int) keys.get(0).asNumber(), 0);

        assertNotNull(keys.get(1));
        assertTrue(keys.get(1).isString());
        assertEquals(keys.get(1).asString(), "test");
    }

    @Test
    public void translatesArrayKeyUnderABasicKey() throws Exception {
        List<NotedKey> keys = DotNotes.keys("test[0]");

        assertNotNull(keys);
        assertEquals(keys.size(), 2);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "test");

        assertNotNull(keys.get(1));
        assertTrue(keys.get(1).isNumber());
        assertEquals((int) keys.get(1).asNumber(), 0);
    }

    @Test
    public void translatesCompoundKeyUsingSingleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("['test']");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "test");
    }

    @Test
    public void translatesCompoundKeyUsingDoubleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("[\"test\"]");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "test");
    }

    @Test
    public void translatesBasicKeyUnderACompoundKeyUsingSingleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("['test'].test");

        assertNotNull(keys);
        assertEquals(keys.size(), 2);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "test");

        assertNotNull(keys.get(1));
        assertTrue(keys.get(1).isString());
        assertEquals(keys.get(1).asString(), "test");
    }

    @Test
    public void translatesBasicKeyUnderACompoundKeyUsingDoubleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("[\"test\"].test");

        assertNotNull(keys);
        assertEquals(keys.size(), 2);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "test");

        assertNotNull(keys.get(1));
        assertTrue(keys.get(1).isString());
        assertEquals(keys.get(1).asString(), "test");
    }

    @Test
    public void translatesArrayKeyUnderACompoundKeyUsingSingleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("['test'][0]");

        assertNotNull(keys);
        assertEquals(keys.size(), 2);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "test");

        assertNotNull(keys.get(1));
        assertTrue(keys.get(1).isNumber());
        assertEquals((int) keys.get(1).asNumber(), 0);
    }

    @Test
    public void translatesArrayKeyUnderACompoundKeyUsingDoubleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("[\"test\"][0]");

        assertNotNull(keys);
        assertEquals(keys.size(), 2);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "test");

        assertNotNull(keys.get(1));
        assertTrue(keys.get(1).isNumber());
        assertEquals((int) keys.get(1).asNumber(), 0);
    }

    @Test
    public void translatesIntegerKeysUsingSingleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("['0']");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "0");
    }

    @Test
    public void translatesIntegerKeysUsingDoubleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("[\"0\"]");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "0");
    }

    @Test
    public void translatesSpecialKeysUsingSingleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("[']]][[[']");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "]]][[[");
    }

    @Test
    public void translatesSpecialKeysUsingDoubleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("[\"]]][[[\"]");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "]]][[[");
    }

    @Test
    public void translatesMismatchingSingleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("['te'st']");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "te'st");
    }

    @Test
    public void translatesMismatchingDoubleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("[\"te\"st\"]");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "te\"st");
    }

    @Test
    public void translatesDottedSpecialKeysUsingSingleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("['test.test']");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "test.test");
    }

    @Test
    public void translatesDottedSpecialKeysUsingDoubleQuotes() throws Exception {
        List<NotedKey> keys = DotNotes.keys("[\"test.test\"]");

        assertNotNull(keys);
        assertEquals(keys.size(), 1);

        assertNotNull(keys.get(0));
        assertTrue(keys.get(0).isString());
        assertEquals(keys.get(0).asString(), "test.test");
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse empty string!")
    public void throwErrorWhenProvidedNullKey() throws Exception {
        DotNotes.keys(null);
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse empty string!")
    public void throwErrorWhenProvidedEmptyKey() throws Exception {
        DotNotes.keys("");
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse 'test.1' at character '1', column 6!")
    public void throwErrorWhenProvidedInvalidKey() throws Exception {
        DotNotes.keys("test.1");
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse 'test.\\['test']' at character '\\[', column 6!")
    public void throwErrorWhenProvidedInvalidBracketNotation() throws Exception {
        DotNotes.keys("test.['test']");
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse 'test.\\[0]' at character '\\[', column 6!")
    public void throwErrorWhenProvidedInvalidArrayNotation() throws Exception {
        DotNotes.keys("test.[0]");
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse 'test\\[test]' at character 't', column 6!")
    public void throwErrorWhenProvidedInvalidArrayIndexNotation() throws Exception {
        DotNotes.keys("test[test]");
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse key with trailing dot!")
    public void throwErrorWhenProvidedTrailingDot() throws Exception {
        DotNotes.keys("test.");
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse key with trailing bracket!")
    public void throwErrorWhenProvidedTrailingBracket() throws Exception {
        DotNotes.keys("test[");
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse '\\['test]' at character '\\[', column 1!")
    public void throwErrorWhenProvidedUnmatchedQuotes() throws Exception {
        DotNotes.keys("['test]");
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse 'test..test' at character '.', column 6!")
    public void throwErrorWhenProvidedSequentialDots() throws Exception {
        DotNotes.keys("test..test");
    }
}
