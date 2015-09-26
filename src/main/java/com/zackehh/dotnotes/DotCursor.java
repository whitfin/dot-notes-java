package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.zackehh.dotnotes.util.NotedHandler;
import com.zackehh.dotnotes.util.NotedKey;

import java.util.Iterator;

/**
 * This class dedicated to handling iteration through a ${@link JsonNode}
 * (ArrayNode or ObjectNode), and passing values back using a ${@link NotedHandler}.
 * Iteration should happen recursively and build paths as it goes.
 */
class DotCursor {

    /**
     * This class is designed to be used statically - if someone manages
     * to get at it with Reflection, throw an Exception so they stop being
     * stupid.
     */
    private DotCursor(){
        throw new UnsupportedOperationException();
    }

    /**
     * The only public method of this class, `recurse` takes a ${@link JsonNode} to
     * iterate through, and a ${@link NotedHandler} for use when processing each leaf
     * in the tree. The final parameter is a String representing the path down the
     * branches of the node, which is typically set to `null` when this class is called.
     *
     * @param node the JsonNode to traverse
     * @param handler the NotedHandler to use when processing
     * @param path the base path of the current branch
     */
    public static void recurse(JsonNode node, NotedHandler handler, String path){
        // are we using an ObjectNode?
        if(node.isObject()) {

            // grab the Iterator through the fields
            Iterator<String> names = node.fieldNames();

            // loop the keys
            while (names.hasNext()) {

                // process the key and pass the params
                iterate(node, NotedKey.of(names.next()), path, handler);
            }
        } else {

            // iterate through the indexes of an ArrayNode
            for(int i = 0, j = node.size(); i < j; i++){

                // process the key and pass the params
                iterate(node, NotedKey.of(i), path, handler);
            }
        }
    }

    /**
     * A simply method used to aid in recursion done by `recurse`. This method will
     * find a ${@link JsonNode} for the given key in the top nest of the node. If the
     * found node is a ${@link com.fasterxml.jackson.databind.node.ContainerNode}, it is
     * passed back to `recurse` in order to further the recursion - otherwise we simply
     * execute the ${@link NotedHandler}.
     *
     * @param o the object to look through (ArrayNode or ObjectNode)
     * @param k the key to pluck out of the object
     * @param p the path we have currently travelled down
     * @param h the handler to use when processing a tuple
     */
    private static void execOrRecurse(JsonNode o, NotedKey k, String p, NotedHandler h){
        // find a node for the given key
        JsonNode node = findNode(o, k);

        // if we find a Container node
        if(node.isContainerNode()){

            // we should recurse again
            recurse(node, h, p);
        } else {

            // otherwise execute handler
            h.execute(p, node);
        }
    }

    /**
     * Finds a ${@link JsonNode} for the provided ${@link NotedKey}. If none can be
     * found, simply return a ${@link MissingNode} so as to not stop the processing with an
     * Exception. It should almost never return a ${@link MissingNode} (if not never).
     *
     * @param o the object to find a value inside
     * @param k the key to use to find a value
     * @return a found ${@link JsonNode} instance
     */
    private static JsonNode findNode(JsonNode o, NotedKey k){
        // is this an Integer key?
        if(k.isNumber()){
            // if so, check an ArrayNode
            return o.path(k.asNumber());
        }
        // otherwise check an ObjectNode
        return o.path(k.asString());
    }

    /**
     * A simple method to reuse code. Accepts a ${@link JsonNode}, and constructs
     * the next step in the chain by determining whether we're working with an ArrayNode
     * or an ObjectNode at the next link in the chain. Passes results through to the
     * ${@link DotCursor#execOrRecurse(JsonNode, NotedKey, String, NotedHandler)} method.
     *
     * @param o the object to work with
     * @param k the key to use in path creation
     * @param p the path as it currently stands
     * @param h the handler to use when processing tuples
     */
    private static void iterate(JsonNode o, NotedKey k, String p, NotedHandler h){
        // convert the NotedKey to a string
        String key = k.toString();

        // create a stub key based on the current path
        String keyStub = (p == null ? "" : p);

        // are we working on an ArrayNode?
        if (o.isArray()) {

            // if so, adjust path accordingly
            execOrRecurse(o, k, keyStub + '[' + k + ']', h);

            // short-circuit
            return;
        }

        // otherwise, adjust the path for an Object, taking into account special keys
        execOrRecurse(o, k, keyStub + (key.contains(".") ? "['" + key + "']" : p != null ? "." + key : key), h);
    }

}
