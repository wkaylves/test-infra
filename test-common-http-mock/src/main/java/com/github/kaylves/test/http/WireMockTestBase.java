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
        return wireMockServer.baseUrl();
    }

    protected int getPort() {
        return wireMockServer.port();
    }

    protected StubDef stubGet(String url) {
        return new StubDef(wireMockServer, url, "GET");
    }

    protected StubDef stubPost(String url) {
        return new StubDef(wireMockServer, url, "POST");
    }

    protected StubDef stubPut(String url) {
        return new StubDef(wireMockServer, url, "PUT");
    }

    protected StubDef stubDelete(String url) {
        return new StubDef(wireMockServer, url, "DELETE");
    }

    protected StubDef stubPatch(String url) {
        return new StubDef(wireMockServer, url, "PATCH");
    }
}
