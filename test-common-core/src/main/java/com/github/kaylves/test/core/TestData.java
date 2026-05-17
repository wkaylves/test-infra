package com.github.kaylves.test.core;

import java.util.HashMap;
import java.util.Map;

public final class TestData {

    private TestData() {
    }

    public static Map<String, Object> user(Integer id, String name, String email) {
        Map<String, Object> m = new HashMap<>();
        if (id != null) m.put("id", id);
        if (name != null) m.put("name", name);
        if (email != null) m.put("email", email);
        return m;
    }

    public static Map<String, Object> error(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("error", msg);
        return m;
    }

    public static Map<String, Object> message(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("message", msg);
        return m;
    }

    public static Map<String, Object> map(String key, Object value) {
        Map<String, Object> m = new HashMap<>();
        m.put(key, value);
        return m;
    }
}
