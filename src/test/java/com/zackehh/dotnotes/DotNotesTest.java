package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zackehh.dotnotes.util.NotedHandler;
import com.zackehh.dotnotes.util.NotedKey;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.StringJoiner;

public class DotNotesTest {

    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test
    public void parseStringIntoEmptyObjectUsingCreate() throws Exception {
        JsonNode parsedObj = DotNotes.create("this.is.a.test", factory.numberNode(5));

        Assert.assertNotNull(parsedObj);
        Assert.assertTrue(parsedObj.isObject());
        Assert.assertTrue(parsedObj.has("this"));
        Assert.assertTrue(parsedObj.get("this").isObject());
        Assert.assertTrue(parsedObj.get("this").has("is"));
        Assert.assertTrue(parsedObj.get("this").get("is").isObject());
        Assert.assertTrue(parsedObj.get("this").get("is").has("a"));
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").isObject());
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").has("test"));
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").get("test").isNumber());
        Assert.assertEquals(parsedObj.get("this").get("is").get("a").get("test").asInt(), 5);
    }

    @Test
    public void parseStringWithArrayIntoEmptyObjectUsingCreate() throws Exception {
        JsonNode parsedObj = DotNotes.create("this.is.a.test[0].value", factory.numberNode(5));

        Assert.assertNotNull(parsedObj);
        Assert.assertTrue(parsedObj.isObject());
        Assert.assertTrue(parsedObj.has("this"));
        Assert.assertTrue(parsedObj.get("this").isObject());
        Assert.assertTrue(parsedObj.get("this").has("is"));
        Assert.assertTrue(parsedObj.get("this").get("is").isObject());
        Assert.assertTrue(parsedObj.get("this").get("is").has("a"));
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").isObject());
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").has("test"));
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").get("test").isArray());
        Assert.assertEquals(parsedObj.get("this").get("is").get("a").get("test").size(), 1);
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").get("test").get(0).isObject());
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").get("test").get(0).has("value"));
        Assert.assertEquals(parsedObj.get("this").get("is").get("a").get("test").get(0).get("value").asInt(), 5);
    }

    @Test
    public void parseStringIntoExistingObjectUsingCreate() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.with("this").with("is").with("a").put("party", 10);

        JsonNode parsedObj = DotNotes.create("this.is.a.test", factory.numberNode(5), oldObj);

        Assert.assertNotNull(parsedObj);
        Assert.assertTrue(parsedObj.isObject());
        Assert.assertTrue(parsedObj.has("this"));
        Assert.assertTrue(parsedObj.get("this").isObject());
        Assert.assertTrue(parsedObj.get("this").has("is"));
        Assert.assertTrue(parsedObj.get("this").get("is").isObject());
        Assert.assertTrue(parsedObj.get("this").get("is").has("a"));
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").isObject());
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").has("test"));
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").get("test").isNumber());
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").has("party"));
        Assert.assertTrue(parsedObj.get("this").get("is").get("a").get("party").isNumber());
        Assert.assertEquals(parsedObj.get("this").get("is").get("a").get("test").asInt(), 5);
        Assert.assertEquals(parsedObj.get("this").get("is").get("a").get("party").asInt(), 10);
    }

    @Test
    public void convertObjectNodeIntoFlattenedEmptyObject() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.with("test").put("one", 5);

        JsonNode newObj = DotNotes.flatten(oldObj);

        Assert.assertNotNull(newObj);
        Assert.assertTrue(newObj.isObject());
        Assert.assertTrue(newObj.has("test.one"));
        Assert.assertTrue(newObj.get("test.one").isNumber());
        Assert.assertEquals(newObj.get("test.one").asInt(), 5);
    }

    @Test
    public void convertObjectNodeWithArrayIntoFlattenedEmptyObject() throws Exception {
        ArrayNode oldArr = factory.arrayNode();

        oldArr.add(1);
        oldArr.add(2);
        oldArr.add(3);

        ObjectNode oldObj = factory.objectNode();

        oldObj.with("test").put("one", 5);
        oldObj.with("test").set("two", oldArr);

        JsonNode newObj = DotNotes.flatten(oldObj);

        Assert.assertNotNull(newObj);
        Assert.assertTrue(newObj.isObject());

        Assert.assertTrue(newObj.has("test.one"));
        Assert.assertTrue(newObj.get("test.one").isNumber());
        Assert.assertEquals(newObj.get("test.one").asInt(), 5);

        Assert.assertTrue(newObj.has("test.two[0]"));
        Assert.assertTrue(newObj.get("test.two[0]").isNumber());
        Assert.assertEquals(newObj.get("test.two[0]").asInt(), 1);

        Assert.assertTrue(newObj.has("test.two[1]"));
        Assert.assertTrue(newObj.get("test.two[1]").isNumber());
        Assert.assertEquals(newObj.get("test.two[1]").asInt(), 2);

        Assert.assertTrue(newObj.has("test.two[2]"));
        Assert.assertTrue(newObj.get("test.two[2]").isNumber());
        Assert.assertEquals(newObj.get("test.two[2]").asInt(), 3);
    }

    @Test
    public void convertObjectNodeWithComplexValuesIntoFlattenedObject() throws Exception {
        ArrayNode oldArr = factory.arrayNode();

        oldArr.add(1);
        oldArr.add(2);
        oldArr.add(factory.objectNode().put("three", 3));

        ObjectNode oldObj = factory.objectNode();

        oldObj.with("test").put("one", 5);
        oldObj.with("test").set("two", oldArr);

        JsonNode newObj = DotNotes.flatten(oldObj);

        Assert.assertNotNull(newObj);
        Assert.assertTrue(newObj.isObject());

        Assert.assertTrue(newObj.has("test.one"));
        Assert.assertTrue(newObj.get("test.one").isNumber());
        Assert.assertEquals(newObj.get("test.one").asInt(), 5);

        Assert.assertTrue(newObj.has("test.two[0]"));
        Assert.assertTrue(newObj.get("test.two[0]").isNumber());
        Assert.assertEquals(newObj.get("test.two[0]").asInt(), 1);

        Assert.assertTrue(newObj.has("test.two[1]"));
        Assert.assertTrue(newObj.get("test.two[1]").isNumber());
        Assert.assertEquals(newObj.get("test.two[1]").asInt(), 2);

        Assert.assertTrue(newObj.has("test.two[2].three"));
        Assert.assertTrue(newObj.get("test.two[2].three").isNumber());
        Assert.assertEquals(newObj.get("test.two[2].three").asInt(), 3);
    }

    @Test
    public void convertObjectNodeWithSpecialCharactersIntoFlattenedObject() throws Exception {
        ArrayNode oldArr = factory.arrayNode();

        oldArr.add(factory.objectNode().put("test.two", 2));

        ObjectNode oldObj = factory.objectNode();

        oldObj.with("testing").set("test.one", oldArr);

        JsonNode newObj = DotNotes.flatten(oldObj);

        Assert.assertNotNull(newObj);
        Assert.assertTrue(newObj.isObject());
        Assert.assertTrue(newObj.has("testing['test.one'][0]['test.two']"));
        Assert.assertTrue(newObj.get("testing['test.one'][0]['test.two']").isNumber());
        Assert.assertEquals(newObj.get("testing['test.one'][0]['test.two']").asInt(), 2);
    }

    @Test
    public void retrieveValueFromArray() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.set("test", factory.arrayNode().add(factory.objectNode().put("t", 1)));

        JsonNode value = DotNotes.get("test[0]['t']", oldObj);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isNumber());
        Assert.assertEquals(value.asInt(), 1);
    }

    @Test
    public void retrieveValueUsingSimplePath() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.put("test", 5);

        JsonNode value = DotNotes.get("test", oldObj);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isNumber());
        Assert.assertEquals(value.asInt(), 5);
    }

    @Test
    public void retrieveValueUsingDottedPath() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.with("test").put("value", 5);

        JsonNode value = DotNotes.get("test.value", oldObj);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isNumber());
        Assert.assertEquals(value.asInt(), 5);
    }

    @Test
    public void retrieveValueUsingBracesPath() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.with("test").put("value", 5);

        JsonNode value = DotNotes.get("['test']['value']", oldObj);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isNumber());
        Assert.assertEquals(value.asInt(), 5);
    }

    @Test
    public void retrieveValueUsingComplexBracesPath() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.with("te[]st").put("value", 5);

        JsonNode value = DotNotes.get("['te[]st']['value']", oldObj);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isNumber());
        Assert.assertEquals(value.asInt(), 5);
    }

    @Test
    public void retrieveIndexValueUsingDottedArrayPath() throws Exception {
        ArrayNode oldArr = factory.arrayNode();

        oldArr.add(1);
        oldArr.add(2);
        oldArr.add(3);

        ObjectNode oldObj = factory.objectNode();

        oldObj.with("test").set("value", oldArr);

        JsonNode value = DotNotes.get("test.value[2]", oldObj);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isNumber());
        Assert.assertEquals(value.asInt(), 3);
    }

    @Test
    public void retrieveValueUsingDottedArrayPath() throws Exception {
        ArrayNode oldArr = factory.arrayNode();
        ArrayNode oldArr2 = factory.arrayNode();

        oldArr.add(1);
        oldArr.add(2);
        oldArr.add(oldArr2.add(factory.objectNode().put("three", 3)));

        ObjectNode oldObj = factory.objectNode();

        oldObj.with("test").set("value", oldArr);

        JsonNode value = DotNotes.get("test.value[2][0].three", oldObj);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isNumber());
        Assert.assertEquals(value.asInt(), 3);
    }

    @Test
    public void retrieveValueUsingDottedAndSingleQuotedArrayPath() throws Exception {
        ArrayNode oldArr = factory.arrayNode();
        ObjectNode oldObj2 = factory.objectNode();

        oldArr.add(1);
        oldArr.add(2);
        oldArr.add(oldObj2.set("three", (factory.objectNode().put("four", 3))));

        ObjectNode oldObj = factory.objectNode();

        oldObj.with("test").set("value", oldArr);

        JsonNode value = DotNotes.get("test.value[2]['three'].four", oldObj);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isNumber());
        Assert.assertEquals(value.asInt(), 3);
    }

    @Test
    public void retrieveValueUsingDottedAndDoubleQuotedArrayPath() throws Exception {
        ArrayNode oldArr = factory.arrayNode();
        ObjectNode oldObj2 = factory.objectNode();

        oldArr.add(1);
        oldArr.add(2);
        oldArr.add(oldObj2.set("three", (factory.objectNode().put("four", 3))));

        ObjectNode oldObj = factory.objectNode();

        oldObj.with("test").set("value", oldArr);

        JsonNode value = DotNotes.get("test.value[2][\"three\"].four", oldObj);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isNumber());
        Assert.assertEquals(value.asInt(), 3);
    }

    @Test
    public void retrieveValueUsingSpecialPath() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.with("test").with("of").put("value", 5);

        JsonNode valueOne = DotNotes.get("test.of['value']", oldObj);

        Assert.assertNotNull(valueOne);
        Assert.assertTrue(valueOne.isNumber());
        Assert.assertEquals(valueOne.asInt(), 5);

        JsonNode valueTwo = DotNotes.get("test['of'][\"value\"]", oldObj);

        Assert.assertNotNull(valueTwo);
        Assert.assertTrue(valueTwo.isNumber());
        Assert.assertEquals(valueTwo.asInt(), 5);

        JsonNode valueThree = DotNotes.get("[\"test\"][\"of\"].value", oldObj);

        Assert.assertNotNull(valueThree);
        Assert.assertTrue(valueThree.isNumber());
        Assert.assertEquals(valueThree.asInt(), 5);
    }

    @Test
    public void retrieveValueHandlesMissingKeys() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.put("test", 5);

        JsonNode value = DotNotes.get("test.one", oldObj);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isMissingNode());
    }

    @Test
    public void retrieveValueHandlesMissingValues() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.with("test");

        JsonNode value = DotNotes.get("test.one.two", oldObj);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isMissingNode());
    }

    @Test
    public void retrieveValueHandlesNullValues() throws Exception {
        JsonNode value = DotNotes.get("test.one.two", null);

        Assert.assertNotNull(value);
        Assert.assertTrue(value.isMissingNode());
    }

    @Test
    public void inflateFlatObjectIntoNest() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.put("this.is.a.test", 5);

        JsonNode newObj = DotNotes.inflate(oldObj);

        Assert.assertNotNull(newObj);
        Assert.assertTrue(newObj.isObject());

        Assert.assertTrue(newObj.has("this"));
        Assert.assertTrue(newObj.get("this").isObject());
        Assert.assertTrue(newObj.get("this").has("is"));
        Assert.assertTrue(newObj.get("this").get("is").isObject());
        Assert.assertTrue(newObj.get("this").get("is").has("a"));
        Assert.assertTrue(newObj.get("this").get("is").get("a").isObject());
        Assert.assertTrue(newObj.get("this").get("is").get("a").has("test"));
        Assert.assertTrue(newObj.get("this").get("is").get("a").get("test").isNumber());

        Assert.assertEquals(newObj.get("this").get("is").get("a").get("test").asInt(), 5);
    }

    @Test
    public void inflateFlatObjectIntoNestMultipleKeys() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.put("first.one", 5);
        oldObj.put("second.one", 5);

        JsonNode newObj = DotNotes.inflate(oldObj);

        Assert.assertNotNull(newObj);
        Assert.assertTrue(newObj.isObject());

        Assert.assertTrue(newObj.has("first"));
        Assert.assertTrue(newObj.get("first").isObject());
        Assert.assertTrue(newObj.get("first").has("one"));
        Assert.assertTrue(newObj.get("first").get("one").isNumber());
        Assert.assertEquals(newObj.get("first").get("one").asInt(), 5);

        Assert.assertTrue(newObj.has("second"));
        Assert.assertTrue(newObj.get("second").isObject());
        Assert.assertTrue(newObj.get("second").has("one"));
        Assert.assertTrue(newObj.get("second").get("one").isNumber());
        Assert.assertEquals(newObj.get("second").get("one").asInt(), 5);
    }

    @Test
    public void inflateFlatObjectIntoNestMultipleKeysSameNests() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.put("test.one", 5);
        oldObj.put("test.two", 5);

        JsonNode newObj = DotNotes.inflate(oldObj);

        Assert.assertNotNull(newObj);
        Assert.assertTrue(newObj.isObject());

        Assert.assertTrue(newObj.has("test"));
        Assert.assertTrue(newObj.get("test").isObject());

        Assert.assertTrue(newObj.get("test").has("one"));
        Assert.assertTrue(newObj.get("test").get("one").isNumber());
        Assert.assertEquals(newObj.get("test").get("one").asInt(), 5);

        Assert.assertTrue(newObj.get("test").has("two"));
        Assert.assertTrue(newObj.get("test").get("two").isNumber());
        Assert.assertEquals(newObj.get("test").get("two").asInt(), 5);
    }

    @Test
    public void inflateFlatObjectIntoNestWithTarget() throws Exception {
        ObjectNode oldObj = factory.objectNode();

        oldObj.put("test.one", 5);

        ObjectNode target = factory.objectNode();

        target.with("test").put("two", 5);

        JsonNode newObj = DotNotes.inflate(oldObj, target);

        Assert.assertNotNull(newObj);
        Assert.assertTrue(newObj.isObject());

        Assert.assertTrue(newObj.has("test"));
        Assert.assertTrue(newObj.get("test").isObject());

        Assert.assertTrue(newObj.get("test").has("one"));
        Assert.assertTrue(newObj.get("test").get("one").isNumber());
        Assert.assertEquals(newObj.get("test").get("one").asInt(), 5);

        Assert.assertTrue(newObj.get("test").has("two"));
        Assert.assertTrue(newObj.get("test").get("two").isNumber());
        Assert.assertEquals(newObj.get("test").get("two").asInt(), 5);
    }

    @Test
    public void keyParsingParsesVariousKeys() throws Exception {
        String s = "this.is.a.test[0]['test'][\"test\"].test[15].test";

        List<NotedKey> keys = DotNotes.keys(s);

        Assert.assertNotNull(keys);
        Assert.assertEquals(keys.size(), 10);

        StringJoiner sj = new StringJoiner(",");
        for(NotedKey key : keys){
            sj.add(key.toString());
        }

        Assert.assertEquals(sj.toString(), "this,is,a,test,0,test,test,test,15,test");
    }

    @Test
    public void keyParsingMaintainsIntegerKeys() throws Exception {
        String s = "test[5].10";

        List<NotedKey> keys = DotNotes.keys(s);

        Assert.assertNotNull(keys);
        Assert.assertEquals(keys.size(), 3);
        Assert.assertTrue(keys.get(1).isNumber());
        Assert.assertTrue(keys.get(2).isString());
    }

    @Test
    public void iterationSkipsMissingNodes() throws Exception {
        ObjectNode objectNode = factory.objectNode();

        objectNode.put("test1", 5);
        objectNode.set("test2", MissingNode.getInstance());

        final int[] count = {0};

        DotNotes.notedCursor(objectNode, new NotedHandler() {
            @Override
            public void execute(String path, JsonNode value) {
                count[0]++;
            }
        });

        Assert.assertEquals(count[0], 1);
    }

    @Test(expectedExceptions = InvocationTargetException.class)
    public void creationOfDotNotesThrowsUnsupportedException() throws Exception {
        Constructor<DotNotes> constructor = DotNotes.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test(expectedExceptions = InvocationTargetException.class)
    public void creationOfDotUtilsThrowsUnsupportedException() throws Exception {
        Constructor<DotUtils> constructor = DotUtils.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test(expectedExceptions = InvocationTargetException.class)
    public void creationOfNotedCursorThrowsUnsupportedException() throws Exception {
        Constructor<DotCursor> constructor = DotCursor.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
