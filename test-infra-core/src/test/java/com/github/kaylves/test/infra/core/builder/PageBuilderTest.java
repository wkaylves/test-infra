package com.github.kaylves.test.infra.core.builder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageBuilderTest {

    @Nested
    @DisplayName("Builder API")
    class BuilderApi {

        @Test
        @DisplayName("should build page with content and default pagination")
        void shouldBuildPageWithContentAndDefaults() {
            Page<String> page = PageBuilder.<String>builder()
                    .content("a", "b", "c")
                    .build();

            assertThat(page.getContent()).containsExactly("a", "b", "c");
            assertThat(page.getNumber()).isEqualTo(0);
            assertThat(page.getSize()).isEqualTo(10);
            assertThat(page.getTotalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("should build page with custom pagination")
        void shouldBuildPageWithCustomPagination() {
            Page<String> page = PageBuilder.<String>builder()
                    .content("a", "b")
                    .page(2)
                    .size(5)
                    .total(100)
                    .build();

            assertThat(page.getNumber()).isEqualTo(2);
            assertThat(page.getSize()).isEqualTo(5);
            assertThat(page.getTotalElements()).isEqualTo(100);
            assertThat(page.getTotalPages()).isEqualTo(20);
        }

        @Test
        @DisplayName("should reject null content list")
        void shouldRejectNullContentList() {
            assertThatThrownBy(() -> PageBuilder.<String>builder().content((List<String>) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("content must not be null");
        }
    }

    @Nested
    @DisplayName("Static factory methods")
    class StaticFactory {

        @Test
        @DisplayName("of(varargs) should create page from items")
        void ofVarargs() {
            Page<Integer> page = PageBuilder.of(1, 2, 3);
            assertThat(page.getContent()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("of(List) should create page from list")
        void ofList() {
            Page<String> page = PageBuilder.of(Arrays.asList("x", "y"));
            assertThat(page.getContent()).containsExactly("x", "y");
        }

        @Test
        @DisplayName("empty() should create empty page")
        void empty() {
            Page<Object> page = PageBuilder.empty();
            assertThat(page.getContent()).isEmpty();
            assertThat(page.getTotalElements()).isEqualTo(0);
        }
    }
}
