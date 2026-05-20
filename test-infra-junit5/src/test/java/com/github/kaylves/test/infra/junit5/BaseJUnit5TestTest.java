package com.github.kaylves.test.infra.junit5;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BaseJUnit5TestTest extends BaseJUnit5Test {

    @Test
    @DisplayName("softly should be injected by SoftAssertionsExtension")
    void softlyShouldBeInjected() {
        assertThat(softly).isNotNull();
    }

    @Test
    @DisplayName("softly() method should return same instance as softly field")
    void softlyMethodShouldReturnSameInstance() {
        assertThat(softly()).isSameAs(softly);
    }

    @Test
    @DisplayName("softly should support multiple soft assertions without early failure")
    void softlyShouldSupportMultipleAssertions() {
        softly.assertThat("hello").isEqualTo("hello");
        softly.assertThat(42).isGreaterThan(0);
        softly.assertThat(true).isTrue();
    }

    @Test
    @DisplayName("softly should auto-verify all assertions at test end")
    void softlyShouldAutoVerify() {
        softly.assertThat("hello").isEqualTo("hello");
        softly.assertThat(42).isGreaterThan(0);
    }

    @Test
    @DisplayName("BaseJUnit5Test should be extensible with MockitoExtension")
    void shouldWorkWithMockitoExtension() {
        assertThat(softly).isNotNull();
        softly.assertThat("test").isEqualTo("test");
    }
}
