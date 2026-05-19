package com.github.kaylves.test.infra.spring.mvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@BaseIntegrationTest
class BaseIntegrationTestTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Spring context should be loaded")
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("TestController should be registered as bean")
    void testControllerExists() {
        assertThat(applicationContext.containsBean("testController")).isTrue();
    }
}
