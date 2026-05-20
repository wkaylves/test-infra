package com.github.kaylves.test.infra.spring.mvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WebMvcTest(TestController.class)
class BaseControllerTestTest extends BaseControllerTest {

    @Test
    @DisplayName("performGet should return correct JSON and status")
    void testPerformGet() {
        MvcTestResult result = performGet("/api/test/hello");
        assertThat(result.assert2xx().readString("$.message")).isEqualTo("hello");
    }

    @Test
    @DisplayName("performGet with headers should pass headers to request")
    void testPerformGetWithHeaders() {
        Map<String, String> headers = Collections.singletonMap("X-Custom", "my-value");
        MvcTestResult result = performGet("/api/test/header", headers);
        assertThat(result.assert2xx().readString("$.customHeader")).isEqualTo("my-value");
    }

    @Test
    @DisplayName("performGet with query params should pass params to request")
    void testPerformGetWithQueryParams() {
        Map<String, String> queryParams = Collections.singletonMap("keyword", "java");
        MvcTestResult result = performGet("/api/test/search", null, queryParams);
        assertThat(result.readString("$.keyword")).isEqualTo("java");
    }

    @Test
    @DisplayName("performGet with headers and query params")
    void testPerformGetWithHeadersAndQueryParams() {
        Map<String, String> headers = Collections.singletonMap("X-Custom", "combined");
        Map<String, String> queryParams = Collections.singletonMap("keyword", "test");
        MvcTestResult result = performGet("/api/test/search", headers, queryParams);
        assertThat(result.readString("$.keyword")).isEqualTo("test");
    }

    @Test
    @DisplayName("performPost should echo request body")
    void testPerformPost() {
        String body = "{\"name\":\"test\"}";
        MvcTestResult result = performPost("/api/test/echo", body);
        assertThat(result.assert2xx().readString("$.name")).isEqualTo("test");
    }

    @Test
    @DisplayName("performPost with headers should pass headers to request")
    void testPerformPostWithHeaders() {
        String body = "{\"name\":\"test\"}";
        Map<String, String> headers = Collections.singletonMap("X-Custom", "post-value");
        MvcTestResult result = performPost("/api/test/echo", body, headers);
        assertThat(result.readString("$.name")).isEqualTo("test");
    }

    @Test
    @DisplayName("performPut should update and return")
    void testPerformPut() {
        String body = "{\"name\":\"updated\"}";
        MvcTestResult result = performPut("/api/test/update", body);
        assertThat(result.readString("$.name")).isEqualTo("updated");
        assertThat(result.readBoolean("$.updated")).isTrue();
    }

    @Test
    @DisplayName("performPatch should partial update")
    void testPerformPatch() {
        String body = "{\"field\":\"value\"}";
        MvcTestResult result = performPatch("/api/test/partial", body);
        assertThat(result.readString("$.field")).isEqualTo("value");
        assertThat(result.readBoolean("$.patched")).isTrue();
    }

    @Test
    @DisplayName("performDelete should return deleted flag")
    void testPerformDelete() {
        MvcTestResult result = performDelete("/api/test/remove");
        assertThat(result.readBoolean("$.deleted")).isTrue();
    }

    @Test
    @DisplayName("performDeleteWithBody should send body with delete request")
    void testPerformDeleteWithBody() {
        String body = "{\"id\":1}";
        MvcTestResult result = performDeleteWithBody("/api/test/remove-with-body", body);
        assertThat(result.readBoolean("$.deleted")).isTrue();
        assertThat(result.readInt("$.id")).isEqualTo(1);
    }

    @Test
    @DisplayName("assertStatus should verify HTTP status")
    void testAssertStatus() {
        MvcTestResult result = performGet("/api/test/hello");
        assertThat(result.assertStatus(200)).isSameAs(result);
    }

    @Test
    @DisplayName("assertStatus should throw on mismatch")
    void testAssertStatusMismatch() {
        MvcTestResult result = performGet("/api/test/hello");
        assertThatThrownBy(() -> result.assertStatus(404))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Check HTTP status")
                .hasMessageContaining("404")
                .hasMessageContaining("200");
    }

    @Test
    @DisplayName("assert2xx should verify 2xx status")
    void testAssert2xx() {
        MvcTestResult result = performGet("/api/test/hello");
        assertThat(result.assert2xx()).isSameAs(result);
    }

    @Test
    @DisplayName("assertStatus() should return AssertJ IntegerAssert for fluent chaining")
    void testAssertStatusFluent() {
        MvcTestResult result = performGet("/api/test/hello");
        result.assertStatus().isEqualTo(200);
        result.assertStatus().isBetween(100, 300);
    }

    @Test
    @DisplayName("assertBody() should return AssertJ ObjectAssert for JsonPathMatcher")
    void testAssertBody() {
        MvcTestResult result = performGet("/api/test/hello");
        assertThat(result.assertBody()).isNotNull();
    }

    @Test
    @DisplayName("MvcTestResult body delegation should work")
    void testBodyDelegation() {
        MvcTestResult result = performGet("/api/test/hello");
        assertThat(result.hasPath("$.message")).isTrue();
        assertThat(result.hasPath("$.nonexistent")).isFalse();
    }

    @Test
    @DisplayName("MvcTestResult getBody should return JsonPathMatcher")
    void testGetBody() {
        MvcTestResult result = performGet("/api/test/hello");
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().readString("$.message")).isEqualTo("hello");
    }
}
