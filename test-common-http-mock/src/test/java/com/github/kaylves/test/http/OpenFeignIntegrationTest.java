package com.github.kaylves.test.http;

import feign.FeignException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.kaylves.test.core.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenFeignIntegrationTest extends BaseWireMock {

    private static UserSpringFeignClient client;

    @BeforeAll
    static void setup() {
        startWireMock();
        client = feignClient(UserSpringFeignClient.class);
    }

    @AfterAll
    static void teardown() {
        stopWireMock();
    }

    @Test
    @DisplayName("GET - 预设响应后调用")
    void get_presetResponse() {
        willReturn(UserSpringFeignClient.class, "getUser")
                .response(user(1, "Alice", "alice@example.com"));

        Map<String, Object> result = client.getUser(1L);
        assertThat(result.get("name")).isEqualTo("Alice");
        assertThat(result.get("email")).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("GET - 列表查询")
    void get_listQuery() {
        willReturn(UserSpringFeignClient.class, "listUsers")
                .response(Arrays.asList(user(1, "Alice", null), user(2, "Bob", null)));

        List<Map<String, Object>> users = client.listUsers();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).get("name")).isEqualTo("Alice");
        assertThat(users.get(1).get("name")).isEqualTo("Bob");
    }

    @Test
    @DisplayName("GET - 404 错误")
    void get_404Error() {
        willReturn(UserSpringFeignClient.class, "getUser")
                .response(404, error("not found"));

        assertThatThrownBy(() -> client.getUser(999L))
                .isInstanceOf(FeignException.class);
    }

    @Test
    @DisplayName("GET - 500 错误")
    void get_500Error() {
        willReturn(UserSpringFeignClient.class, "getUser")
                .response(500, error("internal error"));

        assertThatThrownBy(() -> client.getUser(500L))
                .isInstanceOf(FeignException.class);
    }

    @Test
    @DisplayName("POST - 创建用户")
    void post_createUser() {
        willReturn(UserSpringFeignClient.class, "createUser")
                .response(user(3, "Charlie", "charlie@example.com"));

        Map<String, Object> result = client.createUser(user(null, "Charlie", "charlie@example.com"));
        assertThat(result.get("id")).isEqualTo(3);
        assertThat(result.get("name")).isEqualTo("Charlie");
    }

    @Test
    @DisplayName("POST - 400 错误")
    void post_400Error() {
        willReturn(UserSpringFeignClient.class, "createUser")
                .response(400, error("name is required"));

        assertThatThrownBy(() -> client.createUser(map("email", "no-name@example.com")))
                .isInstanceOf(FeignException.class);
    }

    @Test
    @DisplayName("PUT - 更新用户")
    void put_updateUser() {
        willReturn(UserSpringFeignClient.class, "updateUser")
                .response(user(1, "Alice Updated", "alice@example.com"));

        Map<String, Object> result = client.updateUser(1L, map("name", "Alice Updated"));
        assertThat(result.get("name")).isEqualTo("Alice Updated");
    }

    @Test
    @DisplayName("DELETE - 删除用户")
    void delete_deleteUser() {
        willReturn(UserSpringFeignClient.class, "deleteUser")
                .response(message("deleted"));

        Map<String, Object> result = client.deleteUser(1L);
        assertThat(result.get("message")).isEqualTo("deleted");
    }

    // ==================== Form ====================

    @Test
    @DisplayName("POST Form - 表单提交")
    void post_createUserForm() {
        willReturn(UserSpringFeignClient.class, "createUserForm")
                .response(user(4, "Dave", "dave@example.com"));

        Map<String, Object> form = new HashMap<>();
        form.put("name", "Dave");
        form.put("email", "dave@example.com");
        Map<String, Object> result = client.createUserForm(form);
        assertThat(result.get("id")).isEqualTo(4);
        assertThat(result.get("name")).isEqualTo("Dave");
    }

    // ==================== XML ====================

    @Test
    @DisplayName("POST XML - XML 请求体")
    void post_createUserXml() {
        String xmlRequest = "<user><name>Eve</name><email>eve@example.com</email></user>";
        String xmlResponse = "<user><id>5</id><name>Eve</name><email>eve@example.com</email></user>";
        willReturn(UserSpringFeignClient.class, "createUserXml")
                .response(xmlResponse);

        String result = client.createUserXml(xmlRequest);
        assertThat(result).contains("<id>5</id>");
        assertThat(result).contains("<name>Eve</name>");
    }

    @Test
    @DisplayName("GET XML - XML 响应")
    void get_getUserXml() {
        String xmlResponse = "<user><id>1</id><name>Alice</name><email>alice@example.com</email></user>";
        willReturn(UserSpringFeignClient.class, "getUserXml")
                .response(xmlResponse);

        String result = client.getUserXml(1L);
        assertThat(result).contains("<name>Alice</name>");
        assertThat(result).contains("<email>alice@example.com</email>");
    }

    // ==================== Text ====================

    @Test
    @DisplayName("POST Text - 纯文本请求体")
    void post_createUserText() {
        willReturn(UserSpringFeignClient.class, "createUserText")
                .response("User created: Frank");

        String result = client.createUserText("Frank|frank@example.com");
        assertThat(result).isEqualTo("User created: Frank");
    }

    @Test
    @DisplayName("GET Text - 纯文本响应")
    void get_getUserText() {
        willReturn(UserSpringFeignClient.class, "getUserText")
                .response("Alice|alice@example.com");

        String result = client.getUserText(1L);
        assertThat(result).isEqualTo("Alice|alice@example.com");
    }
}
