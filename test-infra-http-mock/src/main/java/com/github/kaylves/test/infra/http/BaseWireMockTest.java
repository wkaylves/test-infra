package com.github.kaylves.test.infra.http;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseWireMockTest extends BaseWireMock {

    @BeforeAll
    protected void startWireMockForJUnit() {
        startWireMock();
    }

    @AfterAll
    protected void stopWireMockForJUnit() {
        stopWireMock();
    }
}
