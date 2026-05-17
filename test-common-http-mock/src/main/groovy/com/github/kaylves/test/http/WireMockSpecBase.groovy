package com.github.kaylves.test.http

import com.github.tomakehurst.wiremock.WireMockServer
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

abstract class WireMockSpecBase extends Specification {

    protected WireMockServer wireMockServer

    def setup() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort())
        wireMockServer.start()
        setupStubs()
    }

    def cleanup() {
        if (wireMockServer != null) {
            wireMockServer.stop()
        }
    }

    protected abstract void setupStubs()

    protected String getBaseUrl() {
        wireMockServer.baseUrl()
    }

    protected int getPort() {
        wireMockServer.port()
    }

    protected StubDef stubGet(String url) {
        new StubDef(wireMockServer, url, "GET")
    }

    protected StubDef stubPost(String url) {
        new StubDef(wireMockServer, url, "POST")
    }

    protected StubDef stubPut(String url) {
        new StubDef(wireMockServer, url, "PUT")
    }

    protected StubDef stubDelete(String url) {
        new StubDef(wireMockServer, url, "DELETE")
    }

    protected StubDef stubPatch(String url) {
        new StubDef(wireMockServer, url, "PATCH")
    }
}