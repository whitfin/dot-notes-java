package com.zackehh.dotnotes.util;

import com.fasterxml.jackson.databind.JsonNode;

public interface NotedHandler {

    void execute(String path, JsonNode value);

}
