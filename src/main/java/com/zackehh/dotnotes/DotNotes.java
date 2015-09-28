package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zackehh.dotnotes.util.NotedHandler;
import com.zackehh.dotnotes.util.NotedKey;
import com.zackehh.dotnotes.util.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

        // figure out the node we're creating into
        JsonNode targetNode = target == null
                ? keys.get(0).isNumber()
                    ? factory.arrayNode()
                    : factory.objectNode()
                : target;

        // store a temporary reference
        JsonNode tmp = targetNode;

        // iterate through all keys (except the last)
        for(int i = 0, j = keys.size() - 1; i < j; i++){

            // grab the current key
            NotedKey key = keys.get(i);

            // figure out the next key, assuming there is one
            NotedKey nextKey = i < j - 1 ? keys.get(i + 1) : null;

            // store a MissingNode instance
            JsonNode local = MissingNode.getInstance();

            // if the key is a String
            if(key.isString()){
                // we'll get from an ObjectNode
                local = tmp.path(key.asString());
            }

            // if the key is a Number
            if(key.isNumber()){
                // we'll get from an ArrayNode
                local = tmp.path(key.asNumber());
            }

            // if we're dealing with a MissingNode
            if(local.isMissingNode()){
                // check if we have a following key
                if(nextKey != null){
                    // set it to either an ObjectNode or an ArrayNode, based on the nextKey
                    DotUtils.set(tmp, key, nextKey.isNumber() ? factory.arrayNode() : factory.objectNode());
                } else {
                    // otherwise spin up an empty ObjectNode
                    DotUtils.set(tmp, key, factory.objectNode());
                }
            }

            // if the key is a String
            if(key.isString()){
                // nest into the ObjectNode
                tmp = tmp.path(key.asString());
            }

            // if the key is a Number
            if(key.isNumber()){
                // nested into the ArrayNode
                tmp = tmp.path(key.asNumber());
            }
        }

        // grab the last key to process
        NotedKey endKey = keys.get(keys.size() - 1);

        // set the value to the final key
        DotUtils.set(tmp, endKey, value);

        // return the target
        return targetNode;
    }

    /**
     * Flattens a ${@link JsonNode} down to a single level, using the ${@link DotCursor}
     * to iterate through the keys, setting the paths as keys in an ${@link ObjectNode}.
     *
     * @param node the ${@link JsonNode} to flatten
     * @return a flattened ${@link ObjectNode}
     */
    public static ObjectNode flatten(JsonNode node){
        // create a node to use as a target
        final ObjectNode targetNode = factory.objectNode();

        // begin iteration through the keys
        notedCursor(node, new NotedHandler() {
            @Override
            public void execute(String path, JsonNode value) {
                // set the key in the target
                targetNode.set(path, value);
            }
        });

        // return the target
        return targetNode;
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
        // ensure that we have a valid JsonNode instance attached
        node = node == null ? MissingNode.getInstance() : node;

        // create a list of keys from the path
        List<NotedKey> keys = keys(path);

        // store a cheap reference
        JsonNode tmp = node;

        // go through every key we have (except the last)
        for(int i = 0, k = keys.size() - 1; i < k; i++){

            // get the current key
            NotedKey key = keys.get(i);

            // if the key is a String
            if(key.isString()){
                // move into an ObjectNode
                tmp = tmp.path(key.asString());
            }

            // if the key is a Number
            if(key.isNumber()){
                // move into an ArrayNode
                tmp = tmp.path(key.asNumber());
            }

            // if we've hit a dead end
            if(tmp.isMissingNode()){
                // short-circuit
                return tmp;
            }
        }

        // get the last key from the list
        NotedKey key = keys.get(keys.size() - 1);

        // if the key is a Number
        if(key.isNumber()){
            // return the ArrayNode index
            return tmp.path(key.asNumber());
        }

        // return the ObjectNode value
        return tmp.path(key.asString());
    }

    /**
     * A wrapper to ${@link DotNotes#inflate(ObjectNode, ObjectNode)}, simply passing
     * through parameters and appending `null` to the argument set. It's a shorthand for lazy
     * programmers (myself included).
     *
     * @param node the ${@link JsonNode} to inflate
     * @return an inflated ${@link JsonNode} instance
     * @throws ParseException if any parsing issues occur
     */
    public static JsonNode inflate(ObjectNode node) throws ParseException {
        return inflate(node, null);
    }

    /**
     * Inflates an ${@link ObjectNode} which has been flattened previously, returning a
     * ${@link JsonNode}. Uses ${@link DotNotes#create(String, JsonNode, JsonNode)} in order to
     * take advantage of methods already available to us.
     *
     * @param node the node being inflated
     * @param target a potential target object to inflate into
     * @return an inflated ${@link JsonNode}
     * @throws ParseException if any parsing issues occur
     */
    public static JsonNode inflate(ObjectNode node, ObjectNode target) throws ParseException {
        // ensure we have a valid target node
        target = target == null ? factory.objectNode() : target;

        // get an Iterator to run through the single layer
        Iterator<Map.Entry<String, JsonNode>> it = node.fields();

        // iterate keys
        while(it.hasNext()){
            // get the next Map.Entry in the Iterator
            Map.Entry<String, JsonNode> entry = it.next();
            // call the create method, passing the key and value
            create(entry.getKey(), entry.getValue(), target);
        }

        // return the inflated target
        return target;
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
        // output keys
        List<NotedKey> keys = new ArrayList<>();

        // position of search
        int len = s.length(), p = 0;

        // move through the string
        while (p < len) {
            // close brace
            int c = s.indexOf(']', p);
            // dot index
            int d = s.indexOf(".", p);
            // brace index
            int b = s.indexOf("[", p);

            // clean key, just add
            if (d == -1 && b == -1 && !DotUtils.isQuote(s.charAt(p))) {
                // the rest of the string is a key
                keys.add(NotedKey.of(s.substring(p, len)));
                // we're done
                break;
            } else if (b == -1 || (d != -1 && d < b)) {
                // check valid key
                if (s.charAt(d + 1) == '.') {
                    // invalid == exception
                    throw new ParseException(s.charAt(d + 1), d + 1, false);
                }
                // push up to the next dot
                if (c == -1 || (d != -1 && d < c)) {
                    keys.add(NotedKey.of(s.substring(p, d)));
                } else {
                    // check for dot or close
                    if (d < c) {
                        d = c + 1;
                    }
                    // check quotes
                    if (DotUtils.isQuote(s.charAt(p))) {
                        // push up to (not including) the quotes
                        keys.add(NotedKey.of(s.substring(p + 1, d - 2)));
                    } else {
                        // put the index value
                        keys.add(NotedKey.of(DotUtils.parseNum(s.substring(p, d - 1))));
                    }
                }
                // start from the dot
                p = d + 1;
            } else {
                if (b > p) {
                    // find the next brace
                    char nb = s.charAt(b - 2);
                    // push up to the brace
                    if (DotUtils.isQuote(nb)) {
                        keys.add(NotedKey.of(s.substring(p + 1, b - 2)));
                    } else {
                        keys.add(NotedKey.of(s.substring(p, b)));
                    }
                    // start from the brace
                    p = b;
                }
                // fetch next char
                char nc = s.charAt(b + 1);
                // check for quotes
                if (!DotUtils.isQuote(nc)){
                    // find the end of the brace
                    c = s.indexOf(']', b);
                    // check invalid sub-strings
                    if (c < 0 || c == b + 1 || !s.substring(b + 1, c).matches("^\\d+$")){
                        throw new ParseException(nc, b + 1, true);
                    }
                    // push the index key found up until the brace
                    keys.add(NotedKey.of(DotUtils.parseNum(s.substring(b + 1, c))));
                    // shift the position to the next section
                    p = c + 2;
                } else {
                    // check for end of the quotes
                    c = s.indexOf(s.charAt(b + 1) + "]", b);
                    // uh oh
                    if (c == -1) {
                        throw new ParseException("Unable to find matching quote at column " + (b + 1) + "!");
                    }
                    // push key until the next point
                    keys.add(NotedKey.of(s.substring(p + 2, c)));
                    // move to the start of the next key
                    p = c + (len > c + 2 && s.charAt(c + 2) == '.' ? 3 : 2);
                }
            }
        }

        // return the key list
        return keys;
    }

    /**
     * Passes the ${@link JsonNode} parameter and ${@link NotedHandler} to the
     * ${@link DotCursor#recurse(JsonNode, NotedHandler, String)} method to do
     * the heavy lifting. This method simply surfaces it to the outside world.
     *
     * @param node the node to iterate through
     * @param handler the handler to use for processing
     */
    public static void notedCursor(JsonNode node, NotedHandler handler){
        DotCursor.recurse(node, handler, null);
    }
}
