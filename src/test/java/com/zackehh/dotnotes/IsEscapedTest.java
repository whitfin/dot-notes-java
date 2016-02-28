package com.zackehh.dotnotes;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class IsEscapedTest {

    @Test
    public void checksIfABasicKeyIsEscaped() throws Exception {
        assertTrue(DotNotes.isEscaped("test"));
    }

    @Test
    public void checksIfAnArrayKeyIsEscaped() throws Exception {
        assertTrue(DotNotes.isEscaped("[0]"));
    }

    @Test
    public void checksIfASingleQuotedKeyIsEscaped() throws Exception {
        assertTrue(DotNotes.isEscaped("['test']"));
    }

    @Test
    public void checksIfADoubleQuotedKeyIsEscaped() throws Exception {
        assertTrue(DotNotes.isEscaped("[\"test\"]"));
    }

    @Test
    public void checksIfABlankKeyIsEscaped() throws Exception {
        assertTrue(DotNotes.isEscaped("[\"\"]"));
    }

    @Test
    public void checksIfAnEmptyKeyIsEscaped() throws Exception {
        assertFalse(DotNotes.isEscaped(""));
    }

    @Test
    public void checksIfANumericKeyIsEscaped() throws Exception {
        assertFalse(DotNotes.isEscaped("5"));
    }

    @Test
    public void checksIfASpecialKeyIsEscaped() throws Exception {
        assertFalse(DotNotes.isEscaped("my-test"));
    }

    @Test
    public void checksIfAnUnwrappedSingleQuotedKeyIsEscaped() throws Exception {
        assertFalse(DotNotes.isEscaped("'test'"));
    }

    @Test
    public void checksIfAnUnwrappedDoubleQuotedKeyIsEscaped() throws Exception {
        assertFalse(DotNotes.isEscaped("\"test\""));
    }

    @Test
    public void checksIfAMissingKeyIsEscaped() throws Exception {
        assertFalse(DotNotes.isEscaped(null));
    }

}
