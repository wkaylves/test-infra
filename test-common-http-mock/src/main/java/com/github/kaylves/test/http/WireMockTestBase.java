package com.github.kaylves.test.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public abstract class WireMockTestBase {

    protected WireMockServer wireMockServer;

    @BeforeEach
    void setUpWireMock() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        setupStubs();
    }

    @AfterEach
    void tearDownWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    protected abstract void setupStubs();

    protected String getBaseUrl() {
        return getWireMockServer().baseUrl();
    }

    protected int getPort() {
        return getWireMockServer().port();
    }

    protected WireMockServer getWireMockServer() {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            throw new IllegalStateException("WireMock server is not running.");
        }
        return wireMockServer;
    }

    protected WireMockStubBuilder wireMock() {
        return WireMockStubBuilder.on(getWireMockServer());
    }

    protected StubDef stubGet(String url) {
        return new StubDef(getWireMockServer(), url, "GET");
    }

    protected StubDef stubPost(String url) {
        return new StubDef(getWireMockServer(), url, "POST");
    }

    protected StubDef stubPut(String url) {
        return new StubDef(getWireMockServer(), url, "PUT");
    }

    protected StubDef stubDelete(String url) {
        return new StubDef(getWireMockServer(), url, "DELETE");
    }

    protected StubDef stubPatch(String url) {
        return new StubDef(getWireMockServer(), url, "PATCH");
    }
}
