package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.zackehh.dotnotes.util.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ParseExceptionTest {

    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test(expectedExceptions = ParseException.class)
    public void keyParsingThrowsErrorOnEmptyBraces() throws Exception {
        String s = "this.is[]";

        try {
            DotNotes.keys(s);
        } catch(ParseException e) {
            Assert.assertEquals(e.getMessage(), "Unable to parse character ']' at column 9! " +
                    "Did you remember to wrap brace keys in quotes?");
            throw e;
        }
    }

    @Test(expectedExceptions = ParseException.class)
    public void keyParsingThrowsErrorOnOpenBrace() throws Exception {
        String s = "this[5";

        try {
            DotNotes.keys(s);
        } catch(ParseException e) {
            Assert.assertEquals(e.getMessage(), "Unable to parse character '5' at column 6! " +
                    "Did you remember to wrap brace keys in quotes?");
            throw e;
        }
    }

    @Test(expectedExceptions = ParseException.class)
    public void keyParsingThrowsErrorOnInvalidIndex() throws Exception {
        String s = "this[5t]";

        try {
            DotNotes.keys(s);
        } catch(ParseException e) {
            Assert.assertEquals(e.getMessage(), "Unable to parse character '5' at column 6! " +
                    "Did you remember to wrap brace keys in quotes?");
            throw e;
        }
    }

    @Test(expectedExceptions = ParseException.class)
    public void keyParsingThrowsErrorOnMismatchingQuotes() throws Exception {
        String s = "this.is['t]";

        try {
            DotNotes.keys(s);
        } catch(ParseException e) {
            Assert.assertEquals(e.getMessage(), "Unable to find matching quote at column 8!");
            throw e;
        }
    }

    @Test(expectedExceptions = ParseException.class)
    public void keyParsingThrowsErrorOnDoubleDots() throws Exception {
        String s = "this..is";

        try {
            DotNotes.keys(s);
        } catch(ParseException e) {
            Assert.assertEquals(e.getMessage(), "Unable to parse character '.' at column 6!");
            throw e;
        }
    }

    @Test(expectedExceptions = ParseException.class)
    public void keyParsingThrowsErrorInvalidBrace() throws Exception {
        String s = "this.[.is";

        try {
            DotNotes.keys(s);
        } catch(ParseException e) {
            Assert.assertEquals(e.getMessage(), "Unable to parse character '.' at column 7! " +
                    "Did you remember to wrap brace keys in quotes?");
            throw e;
        }
    }

    @Test(expectedExceptions = ParseException.class)
    public void keyParsingThrowsErrorNoSubBraces() throws Exception {
        String s = "this[].is";

        try {
            DotNotes.keys(s);
        } catch(ParseException e) {
            Assert.assertEquals(e.getMessage(), "Unable to parse character ']' at column 6! " +
                    "Did you remember to wrap brace keys in quotes?");
            throw e;
        }
    }

    @Test(expectedExceptions = ParseException.class)
    public void keyCreateThrowsErrorInvalidArrayType() throws Exception {
        try {
            DotNotes.create("[0].test", factory.numberNode(5), factory.objectNode());
        } catch(ParseException e) {
            Assert.assertEquals(e.getMessage(), "Expected ArrayNode target for create call!");
            throw e;
        }
    }

    @Test(expectedExceptions = ParseException.class)
    public void keyCreateThrowsErrorInvalidObjectType() throws Exception {
        try {
            DotNotes.create("test[0]", factory.numberNode(5), factory.arrayNode());
        } catch(ParseException e) {
            Assert.assertEquals(e.getMessage(), "Expected ObjectNode target for create call!");
            throw e;
        }
    }

}
