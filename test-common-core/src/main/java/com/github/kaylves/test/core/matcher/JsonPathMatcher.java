package com.github.kaylves.test.core.matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.io.IOException;

public class JsonPathMatcher {

    private final String json;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    private JsonPathMatcher(String json) {
        this.json = json;
    }

    public static JsonPathMatcher from(String json) {
        if (json == null) {
            throw new IllegalArgumentException("json must not be null");
        }
        return new JsonPathMatcher(json);
    }

    public static JsonPathMatcher from(Object object) {
        try {
            return new JsonPathMatcher(DEFAULT_MAPPER.writeValueAsString(object));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    public <T> T read(String path) {
        return JsonPath.read(json, path);
    }

    public String readString(String path) {
        return read(path);
    }

    public Integer readInt(String path) {
        return read(path);
    }

    public Long readLong(String path) {
        return read(path);
    }

    public Boolean readBoolean(String path) {
        return read(path);
    }

    public boolean hasPath(String path) {
        try {
            JsonPath.read(json, path);
            return true;
        } catch (PathNotFoundException e) {
            return false;
        }
    }

    public JsonNode asJsonNode() {
        try {
            return DEFAULT_MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    public String getJson() {
        return json;
    }
}
