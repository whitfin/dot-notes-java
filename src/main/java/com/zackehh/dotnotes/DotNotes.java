package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.util.ArrayList;
import java.util.List;

import static com.zackehh.dotnotes.DotUtils.firstMatch;
import static com.zackehh.dotnotes.DotUtils.matches;

/**
 * The public access for DotNotes. This class allows for manipulation
 * of ${@link JsonNode}s being passed in, either flattening, inflating
 * or processing recursively. Also provides access to several dot notation
 * utility methods, such as key parsing.
 */
public class DotNotes {

    /**
     * A single reference to the ${@link JsonNodeFactory} singleton.
     */
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    /**
     * This class is designed to be used statically - if someone manages
     * to get at it with Reflection, throw an Exception so they stop being
     * stupid.
     */
    private DotNotes(){
        throw new UnsupportedOperationException();
    }

    /**
     * A wrapper to ${@link DotNotes#create(String, JsonNode, JsonNode)}, simply passing
     * through parameters and appending `null` to the argument set. It's a shorthand for lazy
     * programmers (myself included).
     *
     * @param path the path to create
     * @param value the value to set the path to
     * @return the ${@link JsonNode} after key creation
     * @throws ParseException if any parsing issues occur
     */
    public static JsonNode create(String path, JsonNode value) throws ParseException {
        return create(path, value, null);
    }

    /**
     * Uses a dot-noted path in order to create a given value in the given leaf of a tree.
     * This can either create a new ${@link JsonNode} from scratch, or be used to populate
     * a pre-existing one.
     *
     * @param path the path to create
     * @param value the value to set the path to
     * @param target the target ${@link JsonNode} to create into
     * @return the ${@link JsonNode} after key creation
     * @throws ParseException if any parsing issues occur
     */
    public static JsonNode create(String path, JsonNode value, JsonNode target) throws ParseException {
        // parse the path into a List of keys
        List<NotedKey> keys = keys(path);

        // check null target
        if (target == null) {
            target = keys.get(0).isNumber() ? factory.arrayNode() : factory.objectNode();
        }

        // check correct array type
        if (keys.get(0).isNumber() && !target.isArray()) {
            throw new ParseException("Expected ArrayNode target for create call!");
        }

        // check correct object type
        if (keys.get(0).isString() && !target.isObject()) {
            throw new ParseException("Expected ObjectNode target for create call!");
        }

        // store a temporary reference
        JsonNode tmp = target;

        // grab length
        int lastIndex = keys.size() - 1;

        // iterate through all keys (except the last)
        for(int i = 0; i < lastIndex; i++){

            // grab the current key
            NotedKey key = keys.get(i);

            // store a MissingNode instance
            JsonNode local = DotUtils.findNode(tmp, key);

            // if we're dealing with a MissingNode
            if(local.isMissingNode()){
                // set it to either an ObjectNode or an ArrayNode, based on the nextKey
                DotUtils.set(tmp, key, keys.get(i + 1).isNumber() ? factory.arrayNode() : factory.objectNode());
            }

            tmp = DotUtils.findNode(tmp, key);
        }

        // grab the last key to process
        NotedKey endKey = keys.get(lastIndex);

        // set the value to the final key
        DotUtils.set(tmp, endKey, value);

        // return the target
        return target;
    }

    /**
     * Uses a String path to create a List of keys in order to move
     * through a nested ${@link JsonNode} in order to find a specific
     * value. If the value is found, it is returned. If it can not be
     * found, a ${@link MissingNode} will be returned.
     *
     * @param path the path to find the value for
     * @param node the node to use for the search
     * @return a ${@link JsonNode} if found, a ${@link MissingNode} if not
     * @throws ParseException if any parsing issues occur
     */
    public static JsonNode get(String path, JsonNode node) throws ParseException {
        // check for bad targets
        if (node == null) {
            return MissingNode.getInstance();
        }

        // create a list of keys from the path
        List<NotedKey> keys = keys(path);

        // store a cheap reference
        JsonNode tmp = node;

        // grab length
        int lastIndex = keys.size() - 1;

        // go through every key we have (except the last)
        for(int i = 0; i < lastIndex; i++){
            tmp = DotUtils.findNode(tmp, keys.get(i));
            // if we've hit a dead end
            if(tmp.isMissingNode() || tmp.isNull()){
                // short-circuit
                return MissingNode.getInstance();
            }
        }

        // get the last key from the list
        NotedKey key = keys.get(lastIndex);

        // if the key is a Number
        if(key.isNumber()){
            // return the ArrayNode index
            return tmp.path(key.asNumber());
        }

        // return the ObjectNode value
        return tmp.path(key.asString());
    }

    /**
     * Takes a dot-noted String and converts it to a List of keys. This method is by
     * no means perfect, however it's stable enough for most usage. A List of ${@link NotedKey}s
     * will be returned.
     *
     * @param s the String to parse
     * @return a List of ${@link NotedKey}s
     * @throws ParseException if any parsing issues occur
     */
    public static List<NotedKey> keys(String s) throws ParseException {
        // short-circuit if needed
        if(s == null || s.length() == 0){
            throw new ParseException("Unable to parse empty string!");
        }

        // cursor in String
        int position = 0;

        // key list to build into
        List<NotedKey> keys = new ArrayList<>();

        // clone input
        String input = s;

        // process all input
        while(input.length() > 0){
            // try to grab a match
            String prop = DotUtils.firstMatch(input, DotUtils.SEGMENT);

            // exit if no match
            if(prop == null){
                throw new ParseException(input.charAt(0), position);
            }

            NotedKey val;

            // check accessor
            if(matches(prop, DotUtils.ACCESSOR)){
                // create key
                val = NotedKey.of(prop);
            }
            // check index
            else if (matches(prop, DotUtils.INDEX)) {
                // create key of parsed index
                val = NotedKey.of(DotUtils.parseNum(firstMatch(prop, DotUtils.INDEX)));
            }
            // check prop
            else {
                // create key based on prop match
                val = NotedKey.of(firstMatch(prop, DotUtils.PROPERTY));
            }

            // add the key
            keys.add(val);

            // useful lengths
            int inputLen = input.length();
            int propLen = prop.length();

            // store remaining String
            String remainder;

            // if we're done, init to empty
            if (inputLen == propLen) {
                remainder = "";
            } else {
                // trim remainder
                remainder = input.substring(propLen);
                // check following char
                boolean isDot = remainder.charAt(0) == '.';
                // check trailing special char
                if(remainder.length() > 1){
                    // check following char
                    char nextChar = remainder.charAt(1);
                    // exit if invalid char
                    if (!matches(nextChar, isDot ? DotUtils.ACCESSOR : DotUtils.OPENER)) {
                        throw new ParseException(nextChar, position + propLen + 1);
                    }
                } else {
                    // throw exception on trailing special char
                    throw new ParseException("Unable to parse key with trailing " +
                            (isDot ? "dot" : "bracket") + "!");
                }

                // trim trailing dots
                if (isDot) {
                    remainder = remainder.substring(1);
                }
            }

            // shift the cursor
            position += (inputLen - remainder.length());
            // swap the input
            input = remainder;
        }

        // return the keys list
        return keys;
    }

    /**
     * A small wrapper to ${@link #recurse(JsonNode, NodeIterator, String)} to allow omitting
     * a third parameter, passing null instead.
     *
     * @param node the node to pass
     * @param handler the handler to pass
     */
    public static void recurse(JsonNode node, NodeIterator handler) {
        recurse(node, handler, null);
    }

    /**
     * Moves through the provided JsonNode, emitting values to the ${@link NodeIterator#execute(NotedKey, JsonNode, String)}
     * method of a provided handler. A prefix can be provided in order to start all paths with a custom prefix. Paths will
     * not be generated if ${@link NodeIterator#requirePathGeneration()} returns false.
     *
     * @param node the node to iterate through
     * @param handler the handler to emit to
     * @param start the starting prefix String, if any
     */
    public static void recurse(final JsonNode node, final NodeIterator handler, String start){
        // ensure this is a valid container node
        if(!node.isContainerNode()){
            throw new IllegalArgumentException("Non-object provided to `recurse`!");
        }

        // prefixes should default
        if (start == null) {
            start = "";
        }

        // detect array usage
        final boolean isArr = node.isArray();
        final String prefix = start;

        // iterate through every key in this nest, using iterateNode
        DotUtils.iterateNode(node, new DotUtils.KeyHandler() {
            @Override
            public void execute(NotedKey key) {
                // grab both values of the keys
                String kStr = key.asString();
                Integer kNum = key.asNumber();

                // create a StringBuilder
                StringBuilder keystr = new StringBuilder("");

                // if we're making paths
                if (handler.requirePathGeneration()) {
                    // add the prefix
                    keystr.append(prefix);

                    // detect arrays
                    if(isArr){
                        // add the integer index
                        keystr.append("[").append(kNum).append("]");
                    }
                    // detect special strings
                    else if (!matches(kStr, DotUtils.ACCESSOR)) {
                        // add starting bracket
                        keystr.append("[\"");

                        // ensure escaped quotes
                        if (kStr.contains("\"")) {
                            keystr.append(kStr.replace("\"", "\\\""));
                        } else {
                            keystr.append(kStr);
                        }

                        // add closing bracket
                        keystr.append("\"]");
                    }
                    // default
                    else {
                        // add a dot if it's not the first key
                        if(prefix.length() > 0){
                            keystr.append(".");
                        }
                        // add the entire string
                        keystr.append(kStr);
                    }
                }

                // grab next level down
                JsonNode next = DotUtils.findNode(node, key);

                // check for container, another nest
                if (next.isContainerNode()) {
                    // call again using nest
                    recurse(next, handler, keystr.toString());
                    return;
                }

                // emit to the handler
                handler.execute(key, next, keystr.toString());
            }
        });
    }

    /**
     * A very small interface used for processing the iteration
     * through a set of path and key tuples. Used alongside the
     * cursor class.
     */
    abstract public static class NodeIterator {

        /**
         * Receives a string path (in dot notation), and feeds in
         * the value associated (assuming it's not a missing value).
         *
         * @param path the String path to the key
         * @param value the JsonNode associated
         */
        abstract protected void execute(NotedKey key, JsonNode value, String path);

        /**
         * If the path is not being used, forcing this method to return
         * false will disable path generation, allowing for a faster recursion.
         *
         * @return true if paths should be generated
         */
        protected boolean requirePathGeneration() {
            return true;
        }

    }
}
