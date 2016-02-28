package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Small utility class for methods which shouldn't be accessible
 * to the public. Used by ${@link DotNotes} when parsing through a
 * set of keys.
 */
class DotUtils {

    static final Pattern ACCESSOR
            = Pattern.compile("^[a-zA-Z_$][a-zA-Z0-9_$]*$");
    static final Pattern INDEX
            = Pattern.compile("^\\[([0-9]+)]$");
    static final Pattern OPENER
            = Pattern.compile("^(?:[0-9]|\"|')$");
    static final Pattern PROPERTY
            = Pattern.compile("^\\[(?:'|\")(.*)(?:'|\")]$");
    static final Pattern SEGMENT
            = Pattern.compile("^((?:[a-zA-Z_$][a-zA-Z0-9_$]*)|(?:\\[(?:'.*?(?='])'|\".*?(?=\"])\")])|(?:\\[\\d+]))");
    static final Pattern KEY
            = Pattern.compile(SEGMENT.toString() + "$");

    /**
     * This class is designed to be used statically - if someone manages
     * to get at it with Reflection, throw an Exception so they stop being
     * stupid.
     */
    private DotUtils(){
        throw new UnsupportedOperationException();
    }

    /**
     * Simple shorthand to grab the first match of a Pattern/Matcher
     * pair. Takes a String and a Pattern to compare against and returns
     * the first matching group, NOT the first group available.
     *
     * @param s the String to match
     * @param p the Pattern to match using
     * @return the first matched group
     */
    static String firstMatch(String s, Pattern p){
        Matcher m = p.matcher(s);
        return m.find() ? m.group(1) : null;
    }

    /**
     * Shorthand on ${@link #matches(String, Pattern)} to accept a char
     * rather than a complete String. This is here to ensure fastest conversion
     * of char -> String.
     *
     * @param c the char to match
     * @param p the Pattern to match using
     * @return true if the char matches
     */
    static boolean matches(char c, Pattern p){
        return matches(String.valueOf(c), p);
    }

    /**
     * Shorthand to determine whether a provided String matches
     * a provided Pattern. This is an alternative to ${@link java.lang.String#matches(String)}
     * when the Regex is pre-compiled.
     *
     * @param s the String to match
     * @param p the Pattern to match using
     * @return true if the String matches
     */
    static boolean matches(String s, Pattern p){
        return p.matcher(s).find();
    }

    /**
     * Attempts to parse a Number from a String. This is used to parse
     * ArrayNode indices inside ${@link DotNotes}.
     *
     * @param num the number to try and parse
     * @return an ${@link Integer} instance if possible, `null` if not
     */
    static Integer parseNum(String num){
        return Integer.parseInt(num, 10);
    }

    /**
     * Sets a value in a given JsonNode based on the key being passed
     * in. If the key is a String, we should be working with an ObjectNode,
     * and if the key is a Number, we're working with an ArrayNode.
     *
     * This contract should be enforced elsewhere, making this a safe assumption.
     *
     * @param node the node we're working with
     * @param key the key to set in the node
     * @param value the value to set against the key
     */
    static void set(JsonNode node, NotedKey key, JsonNode value){
        if(key.isNumber()) {
            ArrayNode arr = (ArrayNode) node;

            int num = key.asNumber();

            if(num == node.size()){
                arr.add(value);
            } else {
                arr.set(num, value);
            }
        } else {
            ((ObjectNode) node).set(key.asString(), value);
        }
    }

    /**
     * Finds a ${@link JsonNode} for the provided ${@link NotedKey}. The lookup
     * changes based on whether the passed in key is a Number or a String.
     *
     * @param o the object to find a value inside
     * @param k the key to use to find a value
     * @return a found ${@link JsonNode} instance
     */
    static JsonNode findNode(JsonNode o, NotedKey k){
        return k.isNumber() ? o.path(k.asNumber()) : o.path(k.asString());
    }

    /**
     * A trivial implementation of iterating a ${@link JsonNode} without knowing what
     * type of container is being used. If it's an ${@link ObjectNode}, we iterate using
     * the field names, and if it's an ${@link ArrayNode} we iterate using a typical loop.
     *
     * Each key is emitted to a ${@link KeyHandler} for further processing.
     *
     * @param node the node to iterate
     * @param iterator the callback to pass each key to
     */
    static void iterateNode(JsonNode node, KeyHandler iterator) throws ParseException {
        if(node.isObject()) {
            Iterator<String> names = node.fieldNames();

            while (names.hasNext()) {
                iterator.execute(NotedKey.of(names.next()));
            }
        } else {
            for(int i = 0, j = node.size(); i < j; i++){
                iterator.execute(NotedKey.of(i));
            }
        }
    }

    /**
     * A small interface available to use against ${@link #iterateNode(JsonNode, KeyHandler)}.
     */
    interface KeyHandler {
        /**
         * This simply accepts a ${@link NotedKey} instance based on each key pair found during
         * iteration.
         *
         * @param key the NotedKey to process
         */
        void execute(NotedKey key) throws ParseException;
    }
}
