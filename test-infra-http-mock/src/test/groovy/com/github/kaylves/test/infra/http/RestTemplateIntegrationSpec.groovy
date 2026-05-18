package com.github.kaylves.test.infra.http

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

import static com.github.kaylves.test.infra.core.TestData.*

class RestTemplateIntegrationSpec extends BaseWireMockSpec {

    private static final ObjectMapper MAPPER = new ObjectMapper()
    private RestTemplate restTemplate

    private static String toJson(Object obj) {
        MAPPER.writeValueAsString(obj)
    }

    def setupSpec() {
        stubGet("/api/users/1").body(toJson(user(1, "Alice", "alice@example.com"))).stub()
        stubGet("/api/users").body(toJson([
                user(1, "Alice", null),
                user(2, "Bob", null)
        ])).stub()
        stubGet("/api/users/999").status(404).body(toJson(error("user not found"))).stub()
        stubPost("/api/users").requestBody(toJson(user(null, "Charlie", "charlie@example.com")))
                .status(201).body(toJson(user(3, "Charlie", "charlie@example.com"))).stub()
        stubPost("/api/users-500").requestBody(toJson(error("Error")))
                .status(500).body(toJson(error("internal server error"))).stub()
        stubPut("/api/users/1").requestBody(toJson(user(null, "Alice Updated", "alice.new@example.com")))
                .body(toJson(user(1, "Alice Updated", "alice.new@example.com"))).stub()
        stubDelete("/api/users/1").body(toJson(message("user deleted"))).stub()
        stubGet("/api/test-headers").body(toJson(map("ok", true))).stub()
    }

    def setup() {
        restTemplate = new RestTemplate()
    }

    // ==================== GET ====================

    def "GET - 获取单个资源"() {
        when:
        def resp = restTemplate.getForEntity(getBaseUrl() + "/api/users/1", String)

        then:
        resp.statusCodeValue == 200
        resp.body.contains('"name":"Alice"')
    }

    def "GET - 获取资源列表"() {
        when:
        def resp = restTemplate.getForEntity(getBaseUrl() + "/api/users", String)

        then:
        resp.statusCodeValue == 200
        resp.body.contains('"name":"Bob"')
    }

    def "GET - 处理 404"() {
        when:
        restTemplate.getForEntity(getBaseUrl() + "/api/users/999", String)

        then:
        thrown(HttpClientErrorException)
    }

    // ==================== POST ====================

    def "POST - 创建资源 201"() {
        given:
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def entity = new HttpEntity<>(toJson(user(null, "Charlie", "charlie@example.com")), headers)

        when:
        def resp = restTemplate.postForEntity(getBaseUrl() + "/api/users", entity, String)

        then:
        resp.statusCodeValue == 201
        resp.body.contains('"id":3')
    }

    def "POST - 处理 500"() {
        given:
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def entity = new HttpEntity<>(toJson(user(null, "Error", null)), headers)

        when:
        restTemplate.postForEntity(getBaseUrl() + "/api/users-500", entity, String)

        then:
        thrown(Exception)
    }

    // ==================== PUT ====================

    def "PUT - 更新资源"() {
        given:
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def entity = new HttpEntity<>(toJson(user(null, "Alice Updated", "alice.new@example.com")), headers)

        when:
        def resp = restTemplate.exchange(getBaseUrl() + "/api/users/1", HttpMethod.PUT, entity, String)

        then:
        resp.statusCodeValue == 200
        resp.body.contains('"name":"Alice Updated"')
    }

    // ==================== DELETE ====================

    def "DELETE - 删除资源"() {
        when:
        def resp = restTemplate.exchange(getBaseUrl() + "/api/users/1", HttpMethod.DELETE, null, String)

        then:
        resp.statusCodeValue == 200
        resp.body.contains('"message":"user deleted"')
    }

    // ==================== Content-Type ====================

    def "Content-Type 应为 application/json"() {
        when:
        def resp = restTemplate.getForEntity(getBaseUrl() + "/api/test-headers", String)

        then:
        resp.headers.getContentType().toString().contains("application/json")
    }
}