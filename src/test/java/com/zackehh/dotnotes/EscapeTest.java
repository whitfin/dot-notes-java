package com.zackehh.dotnotes;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class EscapeTest {

    @Test
    public void escapeUsingABasicKey() throws Exception {
        String escaped = DotNotes.escape("test");

        assertNotNull(escaped);
        assertEquals(escaped, "test");
    }

    @Test
    public void escapeUsingANumericKey() throws Exception {
        String escaped = DotNotes.escape("0");

        assertNotNull(escaped);
        assertEquals(escaped, "[\"0\"]");
    }

    @Test
    public void escapeUsingANumericKeyIndex() throws Exception {
        String escaped = DotNotes.escape(0);

        assertNotNull(escaped);
        assertEquals(escaped, "[0]");
    }

    @Test
    public void escapeUsingASpecialKey() throws Exception {
        String escaped = DotNotes.escape("my-test");

        assertNotNull(escaped);
        assertEquals(escaped, "[\"my-test\"]");
    }

    @Test
    public void escapeUsingASingleQuotedKey() throws Exception {
        String escaped = DotNotes.escape("'test'");

        assertNotNull(escaped);
        assertEquals(escaped, "[\"'test'\"]");
    }

    @Test
    public void escapeUsingADoubleQuotedKey() throws Exception {
        String escaped = DotNotes.escape("\"test\"");

        assertNotNull(escaped);
        assertEquals(escaped, "[\"\\\"test\\\"\"]");
    }

    @Test
    public void escapeUsingAnEmptyString() throws Exception {
        String escaped = DotNotes.escape("");

        assertNotNull(escaped);
        assertEquals(escaped, "[\"\"]");
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unexpected non-string value provided!")
    public void throwErrorAgainst () throws Exception {
        DotNotes.escape((NotedKey) null);
    }

}
