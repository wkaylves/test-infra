package com.github.kaylves.test.infra.core.builder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultBuilderTest {

    @Nested
    @DisplayName("Builder API")
    class BuilderApi {

        @Test
        @DisplayName("should build result with defaults")
        void shouldBuildWithDefaults() {
            Result<Void> result = ResultBuilder.<Void>builder().build();
            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getMessage()).isEqualTo("success");
            assertThat(result.getData()).isNull();
        }

        @Test
        @DisplayName("should build result with custom values")
        void shouldBuildWithCustomValues() {
            Result<String> result = ResultBuilder.<String>builder()
                    .code(404)
                    .message("not found")
                    .data("some data")
                    .build();
            assertThat(result.getCode()).isEqualTo(404);
            assertThat(result.getMessage()).isEqualTo("not found");
            assertThat(result.getData()).isInstanceOf(String.class);
            assertThat(result.getData()).isEqualTo("some data");
        }
    }

    @Nested
    @DisplayName("Static factory methods")
    class StaticFactory {

        @Test
        @DisplayName("ok() should return 200 success")
        void ok() {
            Result<Void> result = ResultBuilder.ok();
            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getMessage()).isEqualTo("success");
        }

        @Test
        @DisplayName("ok(data) should return 200 with data")
        void okWithData() {
            Result<String> result = ResultBuilder.ok("payload");
            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getData()).isInstanceOf(String.class);
            assertThat(result.getData()).isEqualTo("payload");
        }

        @Test
        @DisplayName("fail(message) should return 500 with message")
        void fail() {
            Result<Void> result = ResultBuilder.fail("error occurred");
            assertThat(result.getCode()).isEqualTo(500);
            assertThat(result.getMessage()).isEqualTo("error occurred");
        }

        @Test
        @DisplayName("fail(code, message) should return custom error code")
        void failWithCode() {
            Result<Void> result = ResultBuilder.fail(400, "bad request");
            assertThat(result.getCode()).isEqualTo(400);
            assertThat(result.getMessage()).isEqualTo("bad request");
        }
    }
}
