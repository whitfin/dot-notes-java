package com.zackehh.dotnotes.util;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A very small interface used for processing the iteration
 * through a set of path and key tuples. Used alongside the
 * cursor class.
 */
public interface NotedHandler {

    /**
     * Receives a string path (in dot notation), and feeds in
     * the value associated (assuming it's not a missing value).
     *
     * @param path the String path to the key
     * @param value the JsonNode associated
     */
    void execute(String path, JsonNode value);

}
