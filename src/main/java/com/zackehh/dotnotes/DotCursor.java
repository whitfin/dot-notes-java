package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.zackehh.dotnotes.util.NotedHandler;

import java.util.Iterator;

class DotCursor {

    private DotCursor(){
        throw new UnsupportedOperationException();
    }

    public static void recurse(JsonNode obj, NotedHandler handler, String path){
        if(obj.isObject()) {
            Iterator<String> names = obj.fieldNames();
            while (names.hasNext()) {
                iterate(obj, names.next(), path, handler);
            }
        } else {
            for(int i = 0; i < obj.size(); i++){
                iterate(obj, i + "", path, handler);
            }
        }
    }

    public static void callOrRecurse(JsonNode o, String k, String p, NotedHandler h){
        JsonNode node = findNode(o, k);
        if(node.isContainerNode()){
            recurse(node, h, p);
        } else {
            h.execute(p, node);
        }
    }

    private static void iterate(JsonNode o, String k, String p, NotedHandler h){
        if (!findNode(o, k).isMissingNode()) {

            String keyStub = (p == null ? "" : p);

            if (o.isArray()) {
                callOrRecurse(o, k, keyStub + '[' + k + ']', h);
                return;
            }

            String append = k.contains(".") ? "['" + k + "']" : p != null ? "." + k : k;

            callOrRecurse(o, k, keyStub + append, h);
        }
    }

    private static JsonNode findNode(JsonNode o, String k){
        Integer intKey = DotUtils.parseNum(k);

        JsonNode gottenNode;

        if(intKey != null){
            gottenNode = o.path(intKey);
        } else {
            gottenNode = o.path(k);
        }

        return gottenNode;
    }

}
