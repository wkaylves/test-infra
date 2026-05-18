package com.github.kaylves.test.infra.http;

import feign.FeignException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.kaylves.test.infra.core.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeignClientIntegrationTest extends BaseWireMockTest {

    private UserFeignClient client;

    @BeforeAll
    void setup() {
        client = feignClient(UserFeignClient.class);
    }

    @Test
    @DisplayName("GET - path param 响应解码")
    void get_pathParamResponse() {
        willReturn(UserFeignClient.class, "getUser")
                .response(user(1, "Alice", "alice@example.com"));

        Map<String, Object> result = client.getUser(1L);
        assertThat(result.get("name")).isEqualTo("Alice");
        assertThat(result.get("email")).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("GET - List 泛型响应解码")
    void get_listResponse() {
        willReturn(UserFeignClient.class, "listUsers")
                .response(Arrays.asList(user(1, "Alice", null), user(2, "Bob", null)));

        List<Map<String, Object>> users = client.listUsers();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).get("name")).isEqualTo("Alice");
        assertThat(users.get(1).get("name")).isEqualTo("Bob");
    }

    @Test
    @DisplayName("GET - 非 2xx 响应抛 FeignException")
    void get_non2xxThrowsFeignException() {
        willReturn(UserFeignClient.class, "getUser")
                .response(404, error("not found"));

        assertThatThrownBy(() -> client.getUser(999L))
                .isInstanceOf(FeignException.class);
    }

    @Test
    @DisplayName("POST - request body 响应解码")
    void post_requestBodyResponse() {
        willReturn(UserFeignClient.class, "createUser")
                .response(user(3, "Charlie", "charlie@example.com"));

        Map<String, Object> result = client.createUser(user(null, "Charlie", "charlie@example.com"));
        assertThat(result.get("id")).isEqualTo(3);
        assertThat(result.get("name")).isEqualTo("Charlie");
    }

    @Test
    @DisplayName("GET - String 响应透传")
    void get_stringResponse() {
        String xmlResponse = "<user><id>1</id><name>Alice</name><email>alice@example.com</email></user>";
        willReturn(UserFeignClient.class, "getUserXml")
                .response(xmlResponse);

        String result = client.getUserXml(1L);
        assertThat(result).contains("<name>Alice</name>");
        assertThat(result).contains("<email>alice@example.com</email>");
    }
}
