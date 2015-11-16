package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class GetTest {

    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test
    public void getUsingBasicKey() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.put("test", 5);

        JsonNode value = DotNotes.get("test", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingBasicNestedKey() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.with("test").put("test", 5);

        JsonNode value = DotNotes.get("test.test", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingArrayKey() throws Exception {
        ArrayNode arrayNode = factory.arrayNode();

        arrayNode.add(5);

        JsonNode value = DotNotes.get("[0]", arrayNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingArrayNestedKey() throws Exception {
        ArrayNode arrayNode = factory.arrayNode();
        ArrayNode innerNode = factory.arrayNode();

        innerNode.add(5);
        arrayNode.add(innerNode);

        JsonNode value = DotNotes.get("[0][0]", arrayNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingBasicKeyUnderArrayKey() throws Exception {
        ArrayNode arrayNode = factory.arrayNode();
        ObjectNode objectNode = factory.objectNode();

        objectNode.put("test", 5);
        arrayNode.add(objectNode);

        JsonNode value = DotNotes.get("[0].test", arrayNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingArrayKeyUnderBasicKey() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.withArray("test").add(5);

        JsonNode value = DotNotes.get("test[0]", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingCompoundKeyUsingSingleQuotes() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.put("test", 5);

        JsonNode value = DotNotes.get("['test']", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingCompoundKeyUsingDoubleQuotes() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.put("test", 5);

        JsonNode value = DotNotes.get("[\"test\"]", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingBasicKeyUnderCompoundKeyUsingSingleQuotes() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.with("test").put("test", 5);

        JsonNode value = DotNotes.get("['test'].test", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingBasicKeyUnderCompoundKeyUsingDoubleQuotes() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.with("test").put("test", 5);

        JsonNode value = DotNotes.get("[\"test\"].test", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingArrayKeyUnderCompoundKeyUsingSingleQuotes() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.withArray("test").add(5);

        JsonNode value = DotNotes.get("['test'][0]", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingArrayKeyUnderCompoundKeyUsingDoubleQuotes() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.withArray("test").add(5);

        JsonNode value = DotNotes.get("[\"test\"][0]", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingIntegerKeyUsingSingleQuotes() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.put("0", 5);

        JsonNode value = DotNotes.get("['0']", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingIntegerKeyUsingDoubleQuotes() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.put("0", 5);

        JsonNode value = DotNotes.get("[\"0\"]", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingSpecialKeyUsingSingleQuotes() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.put("]]][[[", 5);

        JsonNode value = DotNotes.get("[']]][[[']", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingSpecialKeyUsingDoubleQuotes() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.put("]]][[[", 5);

        JsonNode value = DotNotes.get("[\"]]][[[\"]", objectNode);

        assertNotNull(value);
        assertTrue(value.isNumber());
        assertEquals(value.asInt(), 5);
    }

    @Test
    public void getUsingMissingPath() throws Exception {
        JsonNode value = DotNotes.get("test", factory.objectNode());

        assertNotNull(value);
        assertTrue(value.isMissingNode());
    }

    @Test
    public void getUsingMissingTarget() throws Exception {
        JsonNode value = DotNotes.get("test", null);

        assertNotNull(value);
        assertTrue(value.isMissingNode());
    }

    @Test
    public void getUsingMissingNestedTarget() throws Exception {
        JsonNode value = DotNotes.get("test.test", null);

        assertNotNull(value);
        assertTrue(value.isMissingNode());
    }

    @Test
    public void getUsingMissingNestedPath() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.with("test").with("nest");

        JsonNode value = DotNotes.get("test.test.test", objectNode);

        assertNotNull(value);
        assertTrue(value.isMissingNode());
    }

    @Test
    public void getUsingNulledPath() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.with("test").set("test", NullNode.getInstance());

        JsonNode value = DotNotes.get("test.test.test", objectNode);

        assertNotNull(value);
        assertTrue(value.isMissingNode());
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse key starting with '1' at column 1!")
    public void throwErrorWhenProvidedInvalidKey() throws Exception {
        DotNotes.get("123", factory.numberNode(5));
    }

    @Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = "Unable to parse empty string!")
    public void throwErrorWhenProvidedNullKey() throws Exception {
        DotNotes.get(null, factory.numberNode(5));
    }
}
