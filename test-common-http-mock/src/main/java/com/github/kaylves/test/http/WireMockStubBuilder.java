package com.github.kaylves.test.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

public class WireMockStubBuilder {

    private final WireMockServer server;

    WireMockStubBuilder(WireMockServer server) {
        this.server = server;
    }

    public static WireMockStubBuilder on(WireMockServer server) {
        return new WireMockStubBuilder(server);
    }

    public GetStubBuilder get(String url) {
        return new GetStubBuilder(server, url);
    }

    public PostStubBuilder post(String url) {
        return new PostStubBuilder(server, url);
    }

    public PutStubBuilder put(String url) {
        return new PutStubBuilder(server, url);
    }

    public DeleteStubBuilder delete(String url) {
        return new DeleteStubBuilder(server, url);
    }

    public PatchStubBuilder patch(String url) {
        return new PatchStubBuilder(server, url);
    }

    public static class GetStubBuilder {
        private final WireMockServer server;
        private final String url;
        private int status = 200;
        private String body;

        GetStubBuilder(WireMockServer server, String url) {
            this.server = server;
            this.url = url;
        }

        public GetStubBuilder withStatus(int status) {
            this.status = status;
            return this;
        }

        public GetStubBuilder withBody(String body) {
            this.body = body;
            return this;
        }

        public void stub() {
            server.stubFor(
                    com.github.tomakehurst.wiremock.client.WireMock.get(url)
                            .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                                    .withStatus(status)
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(body))
            );
        }
    }

    public static class PostStubBuilder {
        private final WireMockServer server;
        private final String url;
        private int status = 200;
        private String responseBody;
        private String requestBody;

        PostStubBuilder(WireMockServer server, String url) {
            this.server = server;
            this.url = url;
        }

        public PostStubBuilder withRequestBody(String json) {
            this.requestBody = json;
            return this;
        }

        public PostStubBuilder withStatus(int status) {
            this.status = status;
            return this;
        }

        public PostStubBuilder withResponseBody(String body) {
            this.responseBody = body;
            return this;
        }

        public void stub() {
            MappingBuilder mapping = com.github.tomakehurst.wiremock.client.WireMock.post(url);
            if (requestBody != null) {
                mapping.withRequestBody(com.github.tomakehurst.wiremock.client.WireMock.equalToJson(requestBody));
            }
            server.stubFor(
                    mapping.willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                            .withStatus(status)
                            .withHeader("Content-Type", "application/json")
                            .withBody(responseBody))
            );
        }
    }

    public static class PutStubBuilder {
        private final WireMockServer server;
        private final String url;
        private int status = 200;
        private String responseBody;
        private String requestBody;

        PutStubBuilder(WireMockServer server, String url) {
            this.server = server;
            this.url = url;
        }

        public PutStubBuilder withRequestBody(String json) {
            this.requestBody = json;
            return this;
        }

        public PutStubBuilder withStatus(int status) {
            this.status = status;
            return this;
        }

        public PutStubBuilder withResponseBody(String body) {
            this.responseBody = body;
            return this;
        }

        public void stub() {
            MappingBuilder mapping = com.github.tomakehurst.wiremock.client.WireMock.put(url);
            if (requestBody != null) {
                mapping.withRequestBody(com.github.tomakehurst.wiremock.client.WireMock.equalToJson(requestBody));
            }
            server.stubFor(
                    mapping.willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                            .withStatus(status)
                            .withHeader("Content-Type", "application/json")
                            .withBody(responseBody))
            );
        }
    }

    public static class DeleteStubBuilder {
        private final WireMockServer server;
        private final String url;
        private int status = 200;
        private String body;

        DeleteStubBuilder(WireMockServer server, String url) {
            this.server = server;
            this.url = url;
        }

        public DeleteStubBuilder withStatus(int status) {
            this.status = status;
            return this;
        }

        public DeleteStubBuilder withBody(String body) {
            this.body = body;
            return this;
        }

        public void stub() {
            server.stubFor(
                    com.github.tomakehurst.wiremock.client.WireMock.delete(url)
                            .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                                    .withStatus(status)
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(body))
            );
        }
    }

    public static class PatchStubBuilder {
        private final WireMockServer server;
        private final String url;
        private int status = 200;
        private String responseBody;
        private String requestBody;

        PatchStubBuilder(WireMockServer server, String url) {
            this.server = server;
            this.url = url;
        }

        public PatchStubBuilder withRequestBody(String json) {
            this.requestBody = json;
            return this;
        }

        public PatchStubBuilder withStatus(int status) {
            this.status = status;
            return this;
        }

        public PatchStubBuilder withResponseBody(String body) {
            this.responseBody = body;
            return this;
        }

        public void stub() {
            MappingBuilder mapping = com.github.tomakehurst.wiremock.client.WireMock.patch(
                    com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo(url));
            if (requestBody != null) {
                mapping.withRequestBody(com.github.tomakehurst.wiremock.client.WireMock.equalToJson(requestBody));
            }
            server.stubFor(
                    mapping.willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                            .withStatus(status)
                            .withHeader("Content-Type", "application/json")
                            .withBody(responseBody))
            );
        }
    }
}
