package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zackehh.dotnotes.util.NotedKey;

class DotUtils {

    private DotUtils(){
        throw new UnsupportedOperationException();
    }

    public static boolean isQuote(char s){
        return s == '"' || s == '\'';
    }

    public static Integer parseNum(String num){
        try {
            return Integer.parseInt(num);
        } catch(NumberFormatException ignored) {
            return null;
        }
    }

    public static void set(JsonNode base, NotedKey key, JsonNode value){
        if(key.isString()){
            ((ObjectNode) base).set(key.asString(), value);
        }
        if(key.isNumber()){
            ((ArrayNode) base).add(value);
        }
    }
}
