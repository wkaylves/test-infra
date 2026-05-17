package com.github.kaylves.test.core.matcher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonPathMatcherTest {

    private static final String JSON = "{\"name\":\"Alice\",\"age\":30,\"address\":{\"city\":\"Beijing\"},\"tags\":[\"dev\",\"java\"]}";

    @Nested
    @DisplayName("Factory methods")
    class Factory {

        @Test
        @DisplayName("from(String) should create matcher from JSON string")
        void fromString() {
            JsonPathMatcher matcher = JsonPathMatcher.from(JSON);
            assertThat(matcher.readString("$.name")).isEqualTo("Alice");
        }

        @Test
        @DisplayName("from(Object) should serialize and create matcher")
        void fromObject() {
            Map<String, Object> obj = Collections.singletonMap("key", "value");
            JsonPathMatcher matcher = JsonPathMatcher.from(obj);
            assertThat(matcher.readString("$.key")).isEqualTo("value");
        }

        @Test
        @DisplayName("from(null) should throw IllegalArgumentException")
        void fromNull() {
            assertThatThrownBy(() -> JsonPathMatcher.from((String) null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Read operations")
    class ReadOps {

        @Test
        @DisplayName("readString should extract string value")
        void readString() {
            assertThat(JsonPathMatcher.from(JSON).readString("$.name")).isEqualTo("Alice");
        }

        @Test
        @DisplayName("readInt should extract integer value")
        void readInt() {
            assertThat(JsonPathMatcher.from(JSON).readInt("$.age")).isEqualTo(30);
        }

        @Test
        @DisplayName("readBoolean should extract boolean value")
        void readBoolean() {
            String json = "{\"active\":true}";
            assertThat(JsonPathMatcher.from(json).readBoolean("$.active")).isTrue();
        }

        @Test
        @DisplayName("read should extract nested value")
        void readNested() {
            assertThat(JsonPathMatcher.from(JSON).readString("$.address.city")).isEqualTo("Beijing");
        }
    }

    @Nested
    @DisplayName("Path existence")
    class PathExistence {

        @Test
        @DisplayName("hasPath returns true for existing path")
        void hasPathTrue() {
            assertThat(JsonPathMatcher.from(JSON).hasPath("$.name")).isTrue();
        }

        @Test
        @DisplayName("hasPath returns false for non-existing path")
        void hasPathFalse() {
            assertThat(JsonPathMatcher.from(JSON).hasPath("$.nonexistent")).isFalse();
        }
    }

    @Nested
    @DisplayName("JSON node access")
    class JsonNodeAccess {

        @Test
        @DisplayName("asJsonNode should return parsed JsonNode")
        void asJsonNode() {
            JsonPathMatcher matcher = JsonPathMatcher.from(JSON);
            assertThat(matcher.asJsonNode().get("name").asText()).isEqualTo("Alice");
        }

        @Test
        @DisplayName("getJson should return raw JSON string")
        void getJson() {
            JsonPathMatcher matcher = JsonPathMatcher.from(JSON);
            assertThat(matcher.getJson()).contains("Alice");
        }
    }
}
