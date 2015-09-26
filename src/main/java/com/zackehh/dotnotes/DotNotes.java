package com.zackehh.dotnotes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zackehh.dotnotes.util.NotedHandler;
import com.zackehh.dotnotes.util.NotedKey;
import com.zackehh.dotnotes.util.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DotNotes {

    private static final ObjectMapper mapper = new ObjectMapper();

    private DotNotes(){
        throw new UnsupportedOperationException();
    }

    public static void notedCursor(JsonNode obj, NotedHandler handler, String path){
        DotCursor.recurse(obj, handler, path);
    }

    public static void notedCursor(JsonNode obj, NotedHandler handler){
        notedCursor(obj, handler, null);
    }

    public static ObjectNode flatten(JsonNode obj){
        final ObjectNode targetNode
                = mapper.createObjectNode();

        notedCursor(obj, new NotedHandler() {
            @Override
            public void execute(String path, JsonNode value) {
                targetNode.set(path, value);
            }
        });

        return targetNode;
    }

    public static ObjectNode create(String path, JsonNode value) throws ParseException {
        return create(path, value, null);
    }

    public static ObjectNode create(String path, JsonNode value, ObjectNode target) throws ParseException {
        ObjectNode targetNode = target == null
                ? mapper.createObjectNode()
                : target;

        List<NotedKey> keys = keys(path);

        JsonNode tmp = targetNode;

        for(int i = 0, j = keys.size() - 1; i < j; i++){
            NotedKey key = keys.get(i);
            NotedKey sKey = i < j - 1 ? keys.get(i + 1) : null;

            JsonNode local = MissingNode.getInstance();

            if(key.isString()){
                local = tmp.path(key.asString());
            }

            if(key.isNumber()){
                local = tmp.path(key.asNumber());
            }

            if(local.isMissingNode()){
                if(sKey != null){
                    DotUtils.set(tmp, key, sKey.isNumber() ? mapper.createArrayNode() : mapper.createObjectNode());
                } else {
                    DotUtils.set(tmp, key, mapper.createObjectNode());
                }
            }

            if(key.isString()){
                tmp = tmp.path(key.asString());
            }

            if(key.isNumber()){
                tmp = tmp.path(key.asNumber());
            }
        }

        NotedKey endKey = keys.get(keys.size() - 1);

        DotUtils.set(tmp, endKey, value);

        return targetNode;
    }

    public static JsonNode get(String str, JsonNode obj) throws ParseException {
        if(obj == null){
            obj = MissingNode.getInstance();
        }

        List<NotedKey> keys = keys(str);

        JsonNode tmp = obj;

        for (int i = 0, k = keys.size() - 1; i < k; i++){
            NotedKey key = keys.get(i);
            if(key.isString()){
                tmp = tmp.path(key.asString());
            }
            if(key.isNumber()){
                tmp = tmp.path(key.asNumber());
            }
            if(tmp.isMissingNode()){
                return tmp;
            }
        }

        NotedKey key = keys.get(keys.size() - 1);

        if(key.isNumber()){
            return tmp.path(key.asNumber());
        }

        return tmp.path(key.asString());
    }

    public static JsonNode inflate(ObjectNode toInflate) throws ParseException {
        return inflate(toInflate, null);
    }

    public static JsonNode inflate(ObjectNode toInflate, ObjectNode target) throws ParseException {
        ObjectNode targetNode = target == null
                ? mapper.createObjectNode()
                : target;

        Iterator<Map.Entry<String, JsonNode>> entries = toInflate.fields();

        while(entries.hasNext()){
            Map.Entry<String, JsonNode> entry = entries.next();
            create(entry.getKey(), entry.getValue(), targetNode);
        }

        return targetNode;
    }

    public static List<NotedKey> keys(String n) throws ParseException {
        // position of search
        int len = n.length(), p = 0;

        // output keys
        List<NotedKey> keys = new ArrayList<>();

        // move through the string
        while (p < len) {
            // current char
            char cu = n.charAt(p);
            // close brace
            int c;
            // dot index
            int d = n.indexOf(".", p);
            // brace index
            int b = n.indexOf("[", p);

            // clean key, just add
            if (d == -1 && b == -1 && !DotUtils.isQuote(cu)) {
                // the rest of the string is a key
                keys.add(NotedKey.of(n.substring(p, len)));
                // we're done
                break;
            } else if (b == -1 || (d != -1 && d < b)) {
                // check valid key
                if (n.charAt(d + 1) == '.') {
                    // invalid == exception
                    throw new ParseException(n.charAt(d + 1), d + 1, false);
                }
                // get next close brace
                int cb = n.indexOf(']', p);
                // push up to the next dot
                if (cb == -1 || (d != -1 && d < cb)) {
                    keys.add(NotedKey.of(n.substring(p, d)));
                } else {
                    // check for dot or close
                    if (d < cb) {
                        d = cb + 1;
                    }
                    // check quotes
                    if (DotUtils.isQuote(cu)) {
                        // push up to (not including) the quotes
                        keys.add(NotedKey.of(n.substring(p + 1, d - 2)));
                    } else {
                        // put the index value
                        keys.add(NotedKey.of(DotUtils.parseNum(n.substring(p, d - 1))));
                    }
                }
                // start from the dot
                p = d + 1;
            } else {
                if (b > p) {
                    // find the next brace
                    char nb = n.charAt(b - 2);
                    // push up to the brace
                    if (DotUtils.isQuote(nb)) {
                        keys.add(NotedKey.of(n.substring(p + 1, b - 2)));
                    } else {
                        keys.add(NotedKey.of(n.substring(p, b)));
                    }
                    // start from the brace
                    p = b;
                }
                // fetch next char
                char nc = n.charAt(b + 1);
                // check for quotes
                if (!DotUtils.isQuote(nc)){
                    // find the end of the brace
                    c = n.indexOf(']', b);
                    // check invalid sub-strings
                    if (c < 0 || c == b + 1 || !n.substring(b + 1, c).matches("^\\d+$")){
                        throw new ParseException(nc, b + 1, true);
                    }
                    // push the index key found up until the brace
                    keys.add(NotedKey.of(DotUtils.parseNum(n.substring(b + 1, c))));
                    // shift the position to the next section
                    p = c + 2;
                } else {
                    // check for end of the quotes
                    c = n.indexOf(n.charAt(b + 1) + "]", b);
                    // uh oh
                    if (c == -1) {
                        throw new ParseException("Unable to find matching quote at column " + (b + 1) + "!");
                    }
                    // push key until the next point
                    keys.add(NotedKey.of(n.substring(p + 2, c)));
                    // move to the start of the next key
                    p = c + (len > c + 2 && n.charAt(c + 2) == '.' ? 3 : 2);
                }
            }
        }

        return keys;
    }
}
