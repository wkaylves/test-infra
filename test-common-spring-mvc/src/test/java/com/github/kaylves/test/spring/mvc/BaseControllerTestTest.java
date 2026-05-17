package com.github.kaylves.test.spring.mvc;

import com.github.kaylves.test.core.matcher.JsonPathMatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(TestController.class)
class BaseControllerTestTest extends BaseControllerTest {

    @Test
    @DisplayName("performGetAndMatch should return correct JSON")
    void testPerformGetAndMatch() throws Exception {
        JsonPathMatcher matcher = performGetAndMatch("/api/test/hello");
        assertThat(matcher.readString("message")).isEqualTo("hello");
    }

    @Test
    @DisplayName("performPostAndMatch should echo request body")
    void testPerformPostAndMatch() throws Exception {
        String body = "{\"name\":\"test\"}";
        JsonPathMatcher matcher = performPostAndMatch("/api/test/echo", body);
        assertThat(matcher.readString("name")).isEqualTo("test");
    }

    @Test
    @DisplayName("performPutAndMatch should update and return")
    void testPerformPutAndMatch() throws Exception {
        String body = "{\"name\":\"updated\"}";
        JsonPathMatcher matcher = performPutAndMatch("/api/test/update", body);
        assertThat(matcher.readString("name")).isEqualTo("updated");
        assertThat(matcher.readBoolean("updated")).isTrue();
    }

    @Test
    @DisplayName("performDeleteAndMatch should return deleted flag")
    void testPerformDeleteAndMatch() throws Exception {
        JsonPathMatcher matcher = performDeleteAndMatch("/api/test/remove");
        assertThat(matcher.readBoolean("deleted")).isTrue();
    }
}
