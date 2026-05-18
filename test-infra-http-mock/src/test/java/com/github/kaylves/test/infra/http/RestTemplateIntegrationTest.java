package com.github.kaylves.test.infra.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static com.github.kaylves.test.infra.core.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestTemplateIntegrationTest extends BaseWireMockTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private RestTemplate restTemplate;

    private static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    void setupStubs() {
        stubGet("/api/users/1").body(toJson(user(1, "Alice", "alice@example.com"))).stub();
        stubGet("/api/users").body(toJson(Arrays.asList(
                user(1, "Alice", null),
                user(2, "Bob", null)
        ))).stub();
        stubGet("/api/users/999").status(404).body(toJson(error("user not found"))).stub();
        stubPost("/api/users").requestBody(toJson(user(null, "Charlie", "charlie@example.com")))
                .status(201).body(toJson(user(3, "Charlie", "charlie@example.com"))).stub();
        stubPost("/api/users-500").requestBody(toJson(error("Error")))
                .status(500).body(toJson(error("internal server error"))).stub();
        stubPut("/api/users/1").requestBody(toJson(user(null, "Alice Updated", "alice.new@example.com")))
                .body(toJson(user(1, "Alice Updated", "alice.new@example.com"))).stub();
        stubDelete("/api/users/1").body(toJson(message("user deleted"))).stub();
        stubGet("/api/test-headers").body(toJson(map("ok", true))).stub();
    }

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
    }

    @Test
    @DisplayName("GET - 获取单个资源")
    void get_retrieveSingleResource() {
        ResponseEntity<String> resp = restTemplate.getForEntity(getBaseUrl() + "/api/users/1", String.class);
        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).contains("\"name\":\"Alice\"");
    }

    @Test
    @DisplayName("GET - 获取资源列表")
    void get_retrieveResourceList() {
        ResponseEntity<String> resp = restTemplate.getForEntity(getBaseUrl() + "/api/users", String.class);
        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).contains("\"name\":\"Bob\"");
    }

    @Test
    @DisplayName("GET - 处理 404")
    void get_handle404() {
        assertThatThrownBy(() ->
                restTemplate.getForEntity(getBaseUrl() + "/api/users/999", String.class)
        ).isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    @DisplayName("POST - 创建资源 201")
    void post_createResource201() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(toJson(user(null, "Charlie", "charlie@example.com")), headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(getBaseUrl() + "/api/users", entity, String.class);
        assertThat(resp.getStatusCodeValue()).isEqualTo(201);
        assertThat(resp.getBody()).contains("\"id\":3");
    }

    @Test
    @DisplayName("POST - 处理 500")
    void post_handle500() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(toJson(user(null, "Error", null)), headers);
        assertThatThrownBy(() ->
                restTemplate.postForEntity(getBaseUrl() + "/api/users-500", entity, String.class)
        ).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("PUT - 更新资源")
    void put_updateResource() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(toJson(user(null, "Alice Updated", "alice.new@example.com")), headers);
        ResponseEntity<String> resp = restTemplate.exchange(
                getBaseUrl() + "/api/users/1", HttpMethod.PUT, entity, String.class);
        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).contains("\"name\":\"Alice Updated\"");
    }

    @Test
    @DisplayName("DELETE - 删除资源")
    void delete_deleteResource() {
        ResponseEntity<String> resp = restTemplate.exchange(
                getBaseUrl() + "/api/users/1", HttpMethod.DELETE, null, String.class);
        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).contains("\"message\":\"user deleted\"");
    }

    @Test
    @DisplayName("Content-Type 应为 application/json")
    void contentTypeShouldBeApplicationJson() {
        ResponseEntity<String> resp = restTemplate.getForEntity(getBaseUrl() + "/api/test-headers", String.class);
        assertThat(resp.getHeaders().getContentType().toString()).contains("application/json");
    }
}
