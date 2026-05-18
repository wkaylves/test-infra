package com.github.kaylves.test.http;

import com.github.tomakehurst.wiremock.WireMockServer;

public class StubDef {

    private final WireMockStubBuilder.Stub delegate;

    StubDef(WireMockServer server, String url, String method) {
        this.delegate = WireMockStubBuilder.on(server).request(method, url);
    }

    public StubDef body(String body) {
        delegate.withBody(body);
        return this;
    }

    public StubDef jsonBody(Object body) {
        delegate.withJsonBody(body);
        return this;
    }

    public StubDef textBody(String body) {
        delegate.withTextBody(body);
        return this;
    }

    public StubDef xmlBody(String body) {
        delegate.withXmlBody(body);
        return this;
    }

    public StubDef status(int status) {
        delegate.withStatus(status);
        return this;
    }

    public StubDef requestBody(String body) {
        delegate.withJsonRequestBody(body);
        return this;
    }

    public StubDef rawRequestBody(String body) {
        delegate.withRequestBody(body);
        return this;
    }

    public StubDef jsonRequestBody(Object body) {
        delegate.withJsonRequestBody(body);
        return this;
    }

    public StubDef header(String name, String value) {
        delegate.withHeader(name, value);
        return this;
    }

    public StubDef contentType(String contentType) {
        delegate.withContentType(contentType);
        return this;
    }

    public StubDef requestHeader(String name, String value) {
        delegate.withRequestHeader(name, value);
        return this;
    }

    public StubDef queryParam(String name, String value) {
        delegate.withQueryParam(name, value);
        return this;
    }

    public StubDef fixedDelay(int milliseconds) {
        delegate.withFixedDelay(milliseconds);
        return this;
    }

    public void stub() {
        delegate.stub();
    }
}
