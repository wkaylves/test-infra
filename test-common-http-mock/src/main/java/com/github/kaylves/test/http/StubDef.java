package com.github.kaylves.test.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

public class StubDef {

    private final WireMockServer server;
    private final String url;
    private final String method;
    private int status = 200;
    private String responseBody;
    private String requestBody;

    StubDef(WireMockServer server, String url, String method) {
        this.server = server;
        this.url = url;
        this.method = method;
    }

    public StubDef body(String body) {
        this.responseBody = body;
        return this;
    }

    public StubDef status(int status) {
        this.status = status;
        return this;
    }

    public StubDef requestBody(String body) {
        this.requestBody = body;
        return this;
    }

    public void stub() {
        WireMockStubBuilder builder = WireMockStubBuilder.on(server);
        switch (method) {
            case "GET":
                WireMockStubBuilder.GetStubBuilder getBuilder = builder.get(url).withStatus(status);
                if (responseBody != null) getBuilder.withBody(responseBody);
                getBuilder.stub();
                break;
            case "POST":
                WireMockStubBuilder.PostStubBuilder postBuilder = builder.post(url).withStatus(status);
                if (requestBody != null) postBuilder.withRequestBody(requestBody);
                if (responseBody != null) postBuilder.withResponseBody(responseBody);
                postBuilder.stub();
                break;
            case "PUT":
                WireMockStubBuilder.PutStubBuilder putBuilder = builder.put(url).withStatus(status);
                if (requestBody != null) putBuilder.withRequestBody(requestBody);
                if (responseBody != null) putBuilder.withResponseBody(responseBody);
                putBuilder.stub();
                break;
            case "DELETE":
                WireMockStubBuilder.DeleteStubBuilder deleteBuilder = builder.delete(url).withStatus(status);
                if (responseBody != null) deleteBuilder.withBody(responseBody);
                deleteBuilder.stub();
                break;
            case "PATCH":
                WireMockStubBuilder.PatchStubBuilder patchBuilder = builder.patch(url).withStatus(status);
                if (requestBody != null) patchBuilder.withRequestBody(requestBody);
                if (responseBody != null) patchBuilder.withResponseBody(responseBody);
                patchBuilder.stub();
                break;
        }
    }
}