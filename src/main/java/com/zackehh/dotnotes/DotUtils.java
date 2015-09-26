package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zackehh.dotnotes.util.NotedKey;

/**
 * Small utility class for methods which shouldn't be accessible
 * to the public. Used by ${@link DotNotes} when parsing through a
 * set of keys.
 */
class DotUtils {

    /**
     * This class is designed to be used statically - if someone manages
     * to get at it with Reflection, throw an Exception so they stop being
     * stupid.
     */
    private DotUtils(){
        throw new UnsupportedOperationException();
    }

    /**
     * Determines whether a provided char is a quote of some kind (either
     * " or ').
     *
     * @param c the char to check
     * @return true if the char is a quote
     */
    public static boolean isQuote(char c){
        return c == '"' || c == '\'';
    }

    /**
     * Attempts to parse a Number from a String. This is used to parse
     * ArrayNode indices inside ${@link DotNotes}.
     *
     * @param num the number to try and parse
     * @return an ${@link Integer} instance if possible, `null` if not
     */
    public static Integer parseNum(String num){
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
    public static void set(JsonNode node, NotedKey key, JsonNode value){
        // determine if the key is a String
        if(key.isString()){

            // add the key and value to the object
            ((ObjectNode) node).set(key.asString(), value);
        }

        // determine if the key is a Number
        if(key.isNumber()){

            // this is safe due to pre-processing
            // noinspection ConstantConditions
            ((ArrayNode) node).add(value);
        }
    }
}
