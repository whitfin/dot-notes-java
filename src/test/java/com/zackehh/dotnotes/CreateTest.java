package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CreateTest {

    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test
    public void createUsingBasicKey() throws Exception {
        JsonNode parsedObj = DotNotes.create("test", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("test"));
        assertTrue(parsedObj.get("test").isNumber());
        assertEquals(parsedObj.get("test").asInt(), 5);
    }

    @Test
    public void createUsingBasicNestedKey() throws Exception {
        JsonNode parsedObj = DotNotes.create("test.test", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("test"));
        assertTrue(parsedObj.get("test").isObject());
        assertTrue(parsedObj.get("test").has("test"));
        assertTrue(parsedObj.get("test").get("test").isNumber());
        assertEquals(parsedObj.get("test").get("test").asInt(), 5);
    }

    @Test
    public void createUsingArrayKey() throws Exception {
        JsonNode parsedArr = DotNotes.create("[0]", factory.numberNode(5));

        assertNotNull(parsedArr);
        assertTrue(parsedArr.isArray());
        assertEquals(parsedArr.size(), 1);
        assertTrue(parsedArr.get(0).isNumber());
        assertEquals(parsedArr.get(0).asInt(), 5);
    }

    @Test
    public void createUsingNestedArrayKey() throws Exception {
        JsonNode parsedArr = DotNotes.create("[0][0]", factory.numberNode(5));

        assertNotNull(parsedArr);
        assertTrue(parsedArr.isArray());
        assertEquals(parsedArr.size(), 1);
        assertTrue(parsedArr.get(0).isArray());
        assertEquals(parsedArr.get(0).size(), 1);
        assertTrue(parsedArr.get(0).get(0).isNumber());
        assertEquals(parsedArr.get(0).get(0).asInt(), 5);
    }

    @Test
    public void createUsingSpecificArrayIndex() throws Exception {
        ArrayNode arrayNode = factory.arrayNode();

        arrayNode.add(10);
        arrayNode.add(10);
        arrayNode.add(10);

        JsonNode parsedArr = DotNotes.create("[1]", factory.numberNode(5), arrayNode);

        assertNotNull(parsedArr);
        assertTrue(parsedArr.isArray());
        assertEquals(parsedArr.size(), 3);
        assertTrue(parsedArr.get(0).isNumber());
        assertEquals(parsedArr.get(0).asInt(), 10);
        assertTrue(parsedArr.get(1).isNumber());
        assertEquals(parsedArr.get(1).asInt(), 5);
        assertTrue(parsedArr.get(2).isNumber());
        assertEquals(parsedArr.get(2).asInt(), 10);
    }

    @Test
    public void createUsingBasicKeyUnderAnArrayKey() throws Exception {
        JsonNode parsedArr = DotNotes.create("[0].test", factory.numberNode(5));

        assertNotNull(parsedArr);
        assertTrue(parsedArr.isArray());
        assertEquals(parsedArr.size(), 1);
        assertTrue(parsedArr.get(0).isObject());
        assertEquals(parsedArr.get(0).size(), 1);
        assertTrue(parsedArr.get(0).has("test"));
        assertTrue(parsedArr.get(0).get("test").isNumber());
        assertEquals(parsedArr.get(0).get("test").asInt(), 5);
    }

    @Test
    public void createUsingArrayKeyUnderABasicKey() throws Exception {
        JsonNode parsedObj = DotNotes.create("test[0]", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("test"));
        assertTrue(parsedObj.get("test").isArray());
        assertEquals(parsedObj.get("test").size(), 1);
        assertTrue(parsedObj.get("test").get(0).isNumber());
        assertEquals(parsedObj.get("test").get(0).asInt(), 5);
    }

    @Test
    public void createUsingCompoundKeyUsingSingleQuotes() throws Exception {
        JsonNode parsedObj = DotNotes.create("['test']", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("test"));
        assertTrue(parsedObj.get("test").isNumber());
        assertEquals(parsedObj.get("test").asInt(), 5);
    }

    @Test
    public void createUsingCompoundKeyUsingDoubleQuotes() throws Exception {
        JsonNode parsedObj = DotNotes.create("[\"test\"]", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("test"));
        assertTrue(parsedObj.get("test").isNumber());
        assertEquals(parsedObj.get("test").asInt(), 5);
    }

    @Test
    public void createUsingBasicKeyUnderCompoundKeyUsingSingleQuotes() throws Exception {
        JsonNode parsedObj = DotNotes.create("['test'].test", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("test"));
        assertTrue(parsedObj.get("test").isObject());
        assertEquals(parsedObj.get("test").size(), 1);
        assertTrue(parsedObj.get("test").has("test"));
        assertTrue(parsedObj.get("test").get("test").isNumber());
        assertEquals(parsedObj.get("test").get("test").asInt(), 5);
    }

    @Test
    public void createUsingBasicKeyUnderCompoundKeyUsingDoubleQuotes() throws Exception {
        JsonNode parsedObj = DotNotes.create("[\"test\"].test", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("test"));
        assertTrue(parsedObj.get("test").isObject());
        assertEquals(parsedObj.get("test").size(), 1);
        assertTrue(parsedObj.get("test").has("test"));
        assertTrue(parsedObj.get("test").get("test").isNumber());
        assertEquals(parsedObj.get("test").get("test").asInt(), 5);
    }

    @Test
    public void createUsingArrayKeyUnderCompoundKeyUsingSingleQuotes() throws Exception {
        JsonNode parsedObj = DotNotes.create("['test'][0]", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("test"));
        assertTrue(parsedObj.get("test").isArray());
        assertEquals(parsedObj.get("test").size(), 1);
        assertTrue(parsedObj.get("test").get(0).isNumber());
        assertEquals(parsedObj.get("test").get(0).asInt(), 5);
    }

    @Test
    public void createUsingArrayKeyUnderCompoundKeyUsingDoubleQuotes() throws Exception {
        JsonNode parsedObj = DotNotes.create("[\"test\"][0]", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("test"));
        assertTrue(parsedObj.get("test").isArray());
        assertEquals(parsedObj.get("test").size(), 1);
        assertTrue(parsedObj.get("test").get(0).isNumber());
        assertEquals(parsedObj.get("test").get(0).asInt(), 5);
    }

    @Test
    public void createUsingIntegerKeyUsingSingleQuotes() throws Exception {
        JsonNode parsedObj = DotNotes.create("['10']", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("10"));
        assertTrue(parsedObj.get("10").isNumber());
        assertEquals(parsedObj.get("10").asInt(), 5);
    }

    @Test
    public void createUsingIntegerKeyUsingDoubleQuotes() throws Exception {
        JsonNode parsedObj = DotNotes.create("[\"10\"]", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("10"));
        assertTrue(parsedObj.get("10").isNumber());
        assertEquals(parsedObj.get("10").asInt(), 5);
    }

    @Test
    public void createUsingSpecialKeyUsingSingleQuotes() throws Exception {
        JsonNode parsedObj = DotNotes.create("[']]][[[']", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("]]][[["));
        assertTrue(parsedObj.get("]]][[[").isNumber());
        assertEquals(parsedObj.get("]]][[[").asInt(), 5);
    }

    @Test
    public void createUsingSpecialKeyUsingDoubleQuotes() throws Exception {
        JsonNode parsedObj = DotNotes.create("[\"]]][[[\"]", factory.numberNode(5));

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("]]][[["));
        assertTrue(parsedObj.get("]]][[[").isNumber());
        assertEquals(parsedObj.get("]]][[[").asInt(), 5);
    }

    @Test
    public void createMissingKeyInExistingObject() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.put("sing", 10);

        JsonNode parsedObj = DotNotes.create("dance", factory.numberNode(5), objectNode);

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 2);
        assertTrue(parsedObj.has("sing"));
        assertTrue(parsedObj.has("dance"));
        assertTrue(parsedObj.get("sing").isNumber());
        assertTrue(parsedObj.get("dance").isNumber());
        assertEquals(parsedObj.get("sing").asInt(), 10);
        assertEquals(parsedObj.get("dance").asInt(), 5);
    }

    @Test
    public void createExistingKeyInExistingObject() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.put("dance", 10);

        JsonNode parsedObj = DotNotes.create("dance", factory.numberNode(5), objectNode);

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("dance"));
        assertTrue(parsedObj.get("dance").isNumber());
        assertEquals(parsedObj.get("dance").asInt(), 5);
    }

    @Test
    public void createExistingKeyInExistingNestedObject() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.with("dance").put("dance", 10);

        JsonNode parsedObj = DotNotes.create("dance.dance", factory.numberNode(5), objectNode);

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("dance"));
        assertTrue(parsedObj.get("dance").isObject());
        assertTrue(parsedObj.get("dance").has("dance"));
        assertTrue(parsedObj.get("dance").get("dance").isNumber());
        assertEquals(parsedObj.get("dance").get("dance").asInt(), 5);
    }

    @Test
    public void createUsingNullValue() throws Exception {
        JsonNode parsedObj = DotNotes.create("dance", null);

        assertNotNull(parsedObj);
        assertTrue(parsedObj.isObject());
        assertEquals(parsedObj.size(), 1);
        assertTrue(parsedObj.has("dance"));
        assertTrue(parsedObj.get("dance").isNull());
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse key starting with '1' at column 1!")
    public void throwErrorWhenProvidedInvalidKey() throws Exception {
        DotNotes.create("123", factory.numberNode(5));
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse empty string!")
    public void throwErrorWhenProvidedNullKey() throws Exception {
        DotNotes.create(null, factory.numberNode(5));
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Expected ArrayNode target for create call!")
    public void throwErrorAgainstInvalidObjectTarget() throws Exception {
        DotNotes.create("[0]", factory.numberNode(5), factory.objectNode());
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Expected ObjectNode target for create call!")
    public void throwErrorAgainstInvalidArrayTarget() throws Exception {
        DotNotes.create("test", factory.numberNode(5), factory.arrayNode());
    }
}
