package com.github.kaylves.test.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.Map;

import static com.github.kaylves.test.core.TestData.error;
import static com.github.kaylves.test.core.TestData.map;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WireMockStubBuilderTest extends WireMockTestBase {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private RestTemplate restTemplate;

    private static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
    }

    @Override
    protected void setupStubs() {
    }

    // ==================== GET ====================

    @Test
    @DisplayName("GET - 默认 200 返回 body")
    void get_default200WithBody() {
        stubGet("/api/items/1").body(toJson(item(1, "test"))).stub();

        ResponseEntity<String> resp = restTemplate.getForEntity(getBaseUrl() + "/api/items/1", String.class);
        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).contains("\"name\":\"test\"");
    }

    @Test
    @DisplayName("GET - 自定义 404")
    void get_custom404() {
        stubGet("/api/items/999").status(404).body(toJson(error("not found"))).stub();

        assertThatThrownBy(() ->
                restTemplate.getForEntity(getBaseUrl() + "/api/items/999", String.class)
        ).isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    @DisplayName("GET - 204 无内容")
    void get_204NoContent() {
        stubGet("/api/ping").status(204).stub();

        ResponseEntity<String> resp = restTemplate.getForEntity(getBaseUrl() + "/api/ping", String.class);
        assertThat(resp.getStatusCodeValue()).isEqualTo(204);
    }

    // ==================== POST ====================

    @Test
    @DisplayName("POST - 匹配请求体")
    void post_withRequestBodyMatching() {
        stubPost("/api/items").requestBody(toJson(item(null, "new-item")))
                .status(201).body(toJson(item(2, "new-item"))).stub();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(toJson(item(null, "new-item")), headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(getBaseUrl() + "/api/items", entity, String.class);

        assertThat(resp.getStatusCodeValue()).isEqualTo(201);
        assertThat(resp.getBody()).contains("\"id\":2");
    }

    @Test
    @DisplayName("POST - 不匹配请求体，接受任意")
    void post_withoutBodyMatchingAcceptsAny() {
        stubPost("/api/items").body(toJson(item(3, null))).stub();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(toJson(map("anything", "goes")), headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(getBaseUrl() + "/api/items", entity, String.class);

        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).contains("\"id\":3");
    }

    // ==================== PUT ====================

    @Test
    @DisplayName("PUT - 匹配请求体")
    void put_withRequestBodyMatching() {
        stubPut("/api/items/1").requestBody(toJson(item(null, "updated")))
                .body(toJson(item(1, "updated"))).stub();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(toJson(item(null, "updated")), headers);
        ResponseEntity<String> resp = restTemplate.exchange(
                getBaseUrl() + "/api/items/1", HttpMethod.PUT, entity, String.class);

        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).contains("\"name\":\"updated\"");
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("DELETE - 默认 200 返回 body")
    void delete_default200WithBody() {
        stubDelete("/api/items/1").body(toJson(map("deleted", true))).stub();

        ResponseEntity<String> resp = restTemplate.exchange(
                getBaseUrl() + "/api/items/1", HttpMethod.DELETE, null, String.class);
        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).contains("\"deleted\":true");
    }

    @Test
    @DisplayName("DELETE - 204 无内容")
    void delete_204NoContent() {
        stubDelete("/api/items/2").status(204).stub();

        ResponseEntity<String> resp = restTemplate.exchange(
                getBaseUrl() + "/api/items/2", HttpMethod.DELETE, null, String.class);
        assertThat(resp.getStatusCodeValue()).isEqualTo(204);
    }

    // ==================== PATCH ====================

    @Test
    @DisplayName("PATCH - 注册 stub")
    void patch_registerStub() {
        stubPatch("/api/items/1").requestBody(toJson(item(null, "patched")))
                .body(toJson(item(1, "patched"))).stub();

        assertThat(wireMockServer.listAllStubMappings().getMappings()).isNotEmpty();
    }

    // ==================== verify ====================

    @Test
    @DisplayName("验证 stub 被调用")
    void verifyStubWasCalled() {
        stubGet("/api/verify").body(toJson(map("ok", true))).stub();

        restTemplate.getForEntity(getBaseUrl() + "/api/verify", String.class);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/api/verify")));
    }

    @Test
    @DisplayName("多个 stub 共存")
    void multipleStubsCoexist() {
        stubGet("/api/a").body(toJson(map("source", "a"))).stub();
        stubGet("/api/b").body(toJson(map("source", "b"))).stub();

        ResponseEntity<String> a = restTemplate.getForEntity(getBaseUrl() + "/api/a", String.class);
        ResponseEntity<String> b = restTemplate.getForEntity(getBaseUrl() + "/api/b", String.class);

        assertThat(a.getBody()).contains("\"source\":\"a\"");
        assertThat(b.getBody()).contains("\"source\":\"b\"");
    }

    // ==================== helper ====================

    private static Map<String, Object> item(Integer id, String name) {
        Map<String, Object> m = new HashMap<>();
        if (id != null) m.put("id", id);
        if (name != null) m.put("name", name);
        return m;
    }
}
