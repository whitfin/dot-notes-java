package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class RecurseTest {

    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test
    public void iteratesBasicKeys() throws Exception {
        final ObjectNode objectNode = factory.objectNode();

        objectNode.put("one", 1);
        objectNode.put("two", 2);
        objectNode.put("three", 3);

        final int[] iterator = new int[]{0};
        final Iterator<String> keys = objectNode.fieldNames();

        DotNotes.recurse(objectNode, new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {
                iterator[0]++;

                String k = keys.next();
                int v = objectNode.get(k).asInt();

                assertNotNull(key);
                assertTrue(key.isString());
                assertEquals(key.asString(), k);

                assertNotNull(value);
                assertTrue(value.isNumber());
                assertEquals(value.asInt(), v);

                assertNotNull(path);
                assertEquals(path, k);
            }
        });

        assertEquals(iterator[0], 3);
    }

    @Test
    public void iteratesArrayKeys() throws Exception {
        final ArrayNode arrayNode = factory.arrayNode();

        arrayNode.add(1);

        final int[] iterator = new int[]{0};

        DotNotes.recurse(arrayNode, new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {
                iterator[0]++;

                assertNotNull(key);
                assertTrue(key.isNumber());
                assertEquals((int) key.asNumber(), 0);

                assertNotNull(value);
                assertTrue(value.isNumber());
                assertEquals(value.asInt(), 1);

                assertNotNull(path);
                assertEquals(path, "[0]");
            }
        });

        assertEquals(iterator[0], 1);
    }

    @Test
    public void iteratesIntegerKeys() throws Exception {
        final ObjectNode objectNode = factory.objectNode();

        objectNode.put("1", 1);

        final int[] iterator = new int[]{0};
        final Iterator<String> keys = objectNode.fieldNames();
        final List<String> paths = Collections.singletonList("[\"1\"]");

        DotNotes.recurse(objectNode, new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {
                String k = keys.next();
                int v = objectNode.get(k).asInt();
                String p = paths.get(iterator[0]++);

                assertNotNull(key);
                assertTrue(key.isString());
                assertEquals(key.asString(), k);

                assertNotNull(value);
                assertTrue(value.isNumber());
                assertEquals(value.asInt(), v);

                assertNotNull(path);
                assertEquals(path, p);
            }
        });

        assertEquals(iterator[0], 1);
    }

    @Test
    public void iteratesSpecialKeys() throws Exception {
        final ObjectNode objectNode = factory.objectNode();

        objectNode.put("][", 1);
        objectNode.put("\"", 2);
        objectNode.put("'", 3);

        final int[] iterator = new int[]{0};
        final Iterator<String> keys = objectNode.fieldNames();
        final List<String> paths = Arrays.asList("[\"][\"]", "[\"\\\"\"]", "[\"'\"]");

        DotNotes.recurse(objectNode, new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {
                String k = keys.next();
                int v = objectNode.get(k).asInt();
                String p = paths.get(iterator[0]++);

                assertNotNull(key);
                assertTrue(key.isString());
                assertEquals(key.asString(), k);

                assertNotNull(value);
                assertTrue(value.isNumber());
                assertEquals(value.asInt(), v);

                assertNotNull(path);
                assertEquals(path, p);
            }
        });

        assertEquals(iterator[0], 3);
    }

    @Test
    public void iteratesObjectsRecursively() throws Exception {
        final ObjectNode objectNode = factory.objectNode();

        objectNode.with("test").with("nested").put("objects", 1);

        final int[] iterator = new int[]{0};

        DotNotes.recurse(objectNode, new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {
                iterator[0]++;

                assertNotNull(key);
                assertTrue(key.isString());
                assertEquals(key.asString(), "objects");

                assertNotNull(value);
                assertTrue(value.isNumber());
                assertEquals(value.asInt(), 1);

                assertNotNull(path);
                assertEquals(path, "test.nested.objects");
            }
        });

        assertEquals(iterator[0], 1);
    }

    @Test
    public void iterateArraysRecursively() throws Exception {
        final ArrayNode arrayNode = factory.arrayNode();
        final ArrayNode innerNode = factory.arrayNode();

        innerNode.add(1);
        arrayNode.add(innerNode);

        final int[] iterator = new int[]{0};

        DotNotes.recurse(arrayNode, new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {
                iterator[0]++;

                assertNotNull(key);
                assertTrue(key.isNumber());
                assertEquals((int) key.asNumber(), 0);

                assertNotNull(value);
                assertTrue(value.isNumber());
                assertEquals(value.asInt(), 1);

                assertNotNull(path);
                assertEquals(path, "[0][0]");
            }
        });

        assertEquals(iterator[0], 1);
    }

    @Test
    public void iterateArraysAndObjectsRecursively() throws Exception {
        final ObjectNode innerNest = factory.objectNode();

        innerNest.with("recursion").put("0", 1);

        final ObjectNode middleNest = factory.objectNode();

        middleNest.with("test").withArray("nested").add(innerNest);

        final ObjectNode objectNode = factory.objectNode();

        objectNode.withArray("array").add(middleNest);

        final int[] iterator = new int[]{0};

        DotNotes.recurse(objectNode, new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {
                iterator[0]++;

                assertNotNull(key);
                assertTrue(key.isString());
                assertEquals(key.asString(), "0");

                assertNotNull(value);
                assertTrue(value.isNumber());
                assertEquals(value.asInt(), 1);

                assertNotNull(path);
                assertEquals(path, "array[0].test.nested[0].recursion[\"0\"]");
            }
        });

        assertEquals(iterator[0], 1);
    }

    @Test
    public void iteratesMissingValues() throws Exception {
        final ObjectNode objectNode = factory.objectNode();

        objectNode.set("test", MissingNode.getInstance());

        final int[] iterator = new int[]{0};

        DotNotes.recurse(objectNode, new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {
                iterator[0]++;

                assertNotNull(key);
                assertTrue(key.isString());
                assertEquals(key.asString(), "test");

                assertNotNull(value);
                assertTrue(value.isMissingNode());

                assertNotNull(path);
                assertEquals(path, "test");
            }
        });

        assertEquals(iterator[0], 1);
    }

    @Test
    public void iteratesNullValues() throws Exception {
        final ObjectNode objectNode = factory.objectNode();

        objectNode.set("test", NullNode.getInstance());

        final int[] iterator = new int[]{0};

        DotNotes.recurse(objectNode, new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {
                iterator[0]++;

                assertNotNull(key);
                assertTrue(key.isString());
                assertEquals(key.asString(), "test");

                assertNotNull(value);
                assertTrue(value.isNull());

                assertNotNull(path);
                assertEquals(path, "test");
            }
        });

        assertEquals(iterator[0], 1);
    }

    @Test
    public void iteratesUsingACustomPrefix() throws Exception {
        final ObjectNode objectNode = factory.objectNode();

        objectNode.set("test", NullNode.getInstance());

        final int[] iterator = new int[]{0};

        DotNotes.recurse(objectNode, new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {
                iterator[0]++;

                assertNotNull(key);
                assertTrue(key.isString());
                assertEquals(key.asString(), "test");

                assertNotNull(value);
                assertTrue(value.isNull());

                assertNotNull(path);
                assertEquals(path, "prefix.test");
            }
        }, "prefix");

        assertEquals(iterator[0], 1);
    }

    @Test
    public void iteratesWithoutPathGeneration() throws Exception {
        final ObjectNode objectNode = factory.objectNode();

        objectNode.set("test", NullNode.getInstance());

        final int[] iterator = new int[]{0};

        DotNotes.recurse(objectNode, new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {
                iterator[0]++;

                assertNotNull(key);
                assertTrue(key.isString());
                assertEquals(key.asString(), "test");

                assertNotNull(value);
                assertTrue(value.isNull());

                assertNotNull(path);
                assertEquals(path, "");
            }
            @Override
            protected boolean requirePathGeneration(){
                return false;
            }
        });

        assertEquals(iterator[0], 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Non-object provided to `recurse`!")
    public void throwErrorWhenProvidedNonObject() throws Exception {
        DotNotes.recurse(factory.nullNode(), new DotNotes.NodeIterator() {
            @Override
            protected void execute(NotedKey key, JsonNode value, String path) {

            }
        });
    }
}
