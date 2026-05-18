package com.github.kaylves.test.infra.http

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

class WireMockStubBuilderSpec extends WireMockSpecBase {

    private static final ObjectMapper MAPPER = new ObjectMapper()
    private RestTemplate restTemplate

    private static String toJson(Object obj) {
        MAPPER.writeValueAsString(obj)
    }

    @Override
    protected void setupStubs() {
    }

    def setup() {
        restTemplate = new RestTemplate()
    }

    // ==================== GET ====================

    def "GET - 默认 200 返回 body"() {
        given:
        stubGet("/api/items/1").body(toJson(item(1, "test"))).stub()

        when:
        def resp = restTemplate.getForEntity(getBaseUrl() + "/api/items/1", String)

        then:
        resp.statusCodeValue == 200
        resp.body.contains('"name":"test"')
    }

    def "GET - 自定义 404"() {
        given:
        stubGet("/api/items/999").status(404).body(toJson(error("not found"))).stub()

        when:
        restTemplate.getForEntity(getBaseUrl() + "/api/items/999", String)

        then:
        thrown(HttpClientErrorException)
    }

    def "GET - 204 无内容"() {
        given:
        stubGet("/api/ping").status(204).stub()

        when:
        def resp = restTemplate.getForEntity(getBaseUrl() + "/api/ping", String)

        then:
        resp.statusCodeValue == 204
    }

    // ==================== POST ====================

    def "POST - 匹配请求体"() {
        given:
        stubPost("/api/items").requestBody(toJson(item(null, "new-item")))
                .status(201).body(toJson(item(2, "new-item"))).stub()
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def entity = new HttpEntity<>(toJson(item(null, "new-item")), headers)

        when:
        def resp = restTemplate.postForEntity(getBaseUrl() + "/api/items", entity, String)

        then:
        resp.statusCodeValue == 201
        resp.body.contains('"id":2')
    }

    def "POST - 不匹配请求体，接受任意"() {
        given:
        stubPost("/api/items").body(toJson(item(3, null))).stub()
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def entity = new HttpEntity<>(toJson(map("anything", "goes")), headers)

        when:
        def resp = restTemplate.postForEntity(getBaseUrl() + "/api/items", entity, String)

        then:
        resp.statusCodeValue == 200
        resp.body.contains('"id":3')
    }

    // ==================== PUT ====================

    def "PUT - 匹配请求体"() {
        given:
        stubPut("/api/items/1").requestBody(toJson(item(null, "updated")))
                .body(toJson(item(1, "updated"))).stub()
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def entity = new HttpEntity<>(toJson(item(null, "updated")), headers)

        when:
        def resp = restTemplate.exchange(getBaseUrl() + "/api/items/1", HttpMethod.PUT, entity, String)

        then:
        resp.statusCodeValue == 200
        resp.body.contains('"name":"updated"')
    }

    // ==================== DELETE ====================

    def "DELETE - 默认 200 返回 body"() {
        given:
        stubDelete("/api/items/1").body(toJson(map("deleted", true))).stub()

        when:
        def resp = restTemplate.exchange(getBaseUrl() + "/api/items/1", HttpMethod.DELETE, null, String)

        then:
        resp.statusCodeValue == 200
        resp.body.contains('"deleted":true')
    }

    def "DELETE - 204 无内容"() {
        given:
        stubDelete("/api/items/2").status(204).stub()

        when:
        def resp = restTemplate.exchange(getBaseUrl() + "/api/items/2", HttpMethod.DELETE, null, String)

        then:
        resp.statusCodeValue == 204
    }

    // ==================== PATCH ====================

    def "PATCH - 注册 stub"() {
        given:
        stubPatch("/api/items/1").requestBody(toJson(item(null, "patched")))
                .body(toJson(item(1, "patched"))).stub()

        expect:
        wireMockServer.listAllStubMappings().mappings
    }

    // ==================== verify ====================

    def "验证 stub 被调用"() {
        given:
        stubGet("/api/verify").body(toJson(map("ok", true))).stub()

        when:
        restTemplate.getForEntity(getBaseUrl() + "/api/verify", String)

        then:
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/api/verify")))
    }

    def "多个 stub 共存"() {
        given:
        stubGet("/api/a").body(toJson(map("source", "a"))).stub()
        stubGet("/api/b").body(toJson(map("source", "b"))).stub()

        when:
        def a = restTemplate.getForEntity(getBaseUrl() + "/api/a", String)
        def b = restTemplate.getForEntity(getBaseUrl() + "/api/b", String)

        then:
        a.body.contains('"source":"a"')
        b.body.contains('"source":"b"')
    }

    // ==================== helper ====================

    private static Map<String, Object> item(Integer id, String name) {
        def m = new HashMap<String, Object>()
        if (id != null) m.put("id", id)
        if (name != null) m.put("name", name)
        m
    }

    private static Map<String, Object> error(String message) {
        [error: message] as Map<String, Object>
    }

    private static Map<String, Object> map(String key, Object value) {
        [(key): value] as Map<String, Object>
    }
}