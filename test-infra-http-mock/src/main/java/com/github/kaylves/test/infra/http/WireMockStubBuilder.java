package com.github.kaylves.test.infra.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

import java.util.LinkedHashMap;
import java.util.Map;

public class WireMockStubBuilder {

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final WireMockServer server;

    WireMockStubBuilder(WireMockServer server) {
        if (server == null) {
            throw new IllegalArgumentException("WireMockServer must not be null.");
        }
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

    public Stub request(String method, String url) {
        return new Stub(server, method, url);
    }

    private static String toJson(Object body) {
        try {
            return MAPPER.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize body as JSON.", e);
        }
    }

    public static class Stub {
        private final WireMockServer server;
        private final String method;
        private final String url;
        private final Map<String, String> responseHeaders = new LinkedHashMap<>();
        private final Map<String, String> requestHeaders = new LinkedHashMap<>();
        private final Map<String, String> queryParams = new LinkedHashMap<>();
        private int status = 200;
        private Integer fixedDelayMillis;
        private String responseBody;
        private String requestBody;
        private boolean requestBodyIsJson;

        Stub(WireMockServer server, String method, String url) {
            if (method == null || method.trim().isEmpty()) {
                throw new IllegalArgumentException("HTTP method must not be blank.");
            }
            if (url == null || url.trim().isEmpty()) {
                throw new IllegalArgumentException("URL must not be blank.");
            }
            this.server = server;
            this.method = method.toUpperCase();
            this.url = url;
            this.responseHeaders.put("Content-Type", APPLICATION_JSON);
        }

        public Stub withStatus(int status) {
            if (status < 100 || status > 599) {
                throw new IllegalArgumentException("HTTP status must be between 100 and 599.");
            }
            this.status = status;
            return this;
        }

        public Stub withBody(String body) {
            this.responseBody = body;
            return this;
        }

        public Stub withResponseBody(String body) {
            return withBody(body);
        }

        public Stub withJsonBody(Object body) {
            this.responseBody = toJson(body);
            return withContentType(APPLICATION_JSON);
        }

        public Stub withTextBody(String body) {
            this.responseBody = body;
            return withContentType(TEXT_PLAIN);
        }

        public Stub withXmlBody(String body) {
            this.responseBody = body;
            return withContentType(APPLICATION_XML);
        }

        public Stub withRequestBody(String body) {
            this.requestBody = body;
            this.requestBodyIsJson = false;
            return this;
        }

        public Stub withJsonRequestBody(String body) {
            this.requestBody = body;
            this.requestBodyIsJson = true;
            return this;
        }

        public Stub withJsonRequestBody(Object body) {
            return withJsonRequestBody(toJson(body));
        }

        public Stub withHeader(String name, String value) {
            validateNameValue(name, value, "response header");
            this.responseHeaders.put(name, value);
            return this;
        }

        public Stub withContentType(String contentType) {
            validateValue(contentType, "content type");
            this.responseHeaders.put("Content-Type", contentType);
            return this;
        }

        public Stub withRequestHeader(String name, String value) {
            validateNameValue(name, value, "request header");
            this.requestHeaders.put(name, value);
            return this;
        }

        public Stub withQueryParam(String name, String value) {
            validateNameValue(name, value, "query param");
            this.queryParams.put(name, value);
            return this;
        }

        public Stub withFixedDelay(int milliseconds) {
            if (milliseconds < 0) {
                throw new IllegalArgumentException("Fixed delay must not be negative.");
            }
            this.fixedDelayMillis = milliseconds;
            return this;
        }

        public void stub() {
            MappingBuilder mapping = mappingBuilder();

            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                mapping.withQueryParam(entry.getKey(), WireMock.equalTo(entry.getValue()));
            }
            for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                mapping.withHeader(entry.getKey(), WireMock.equalTo(entry.getValue()));
            }
            if (requestBody != null) {
                if (requestBodyIsJson) {
                    mapping.withRequestBody(WireMock.equalToJson(requestBody));
                } else {
                    mapping.withRequestBody(WireMock.equalTo(requestBody));
                }
            }

            ResponseDefinitionBuilder response = WireMock.aResponse().withStatus(status);
            for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
                response.withHeader(entry.getKey(), entry.getValue());
            }
            if (responseBody != null) {
                response.withBody(responseBody);
            }
            if (fixedDelayMillis != null) {
                response.withFixedDelay(fixedDelayMillis);
            }

            server.stubFor(mapping.willReturn(response));
        }

        private MappingBuilder mappingBuilder() {
            UrlPattern urlPattern = url.contains("?") && queryParams.isEmpty()
                    ? WireMock.urlEqualTo(url)
                    : WireMock.urlPathEqualTo(url);
            switch (method) {
                case "GET":
                    return WireMock.get(urlPattern);
                case "POST":
                    return WireMock.post(urlPattern);
                case "PUT":
                    return WireMock.put(urlPattern);
                case "DELETE":
                    return WireMock.delete(urlPattern);
                case "PATCH":
                    return WireMock.patch(urlPattern);
                default:
                    return WireMock.request(method, urlPattern);
            }
        }

        private static void validateNameValue(String name, String value, String label) {
            validateValue(name, label + " name");
            validateValue(value, label + " value");
        }

        private static void validateValue(String value, String label) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException(label + " must not be blank.");
            }
        }
    }

    public static class GetStubBuilder extends Stub {
        GetStubBuilder(WireMockServer server, String url) {
            super(server, "GET", url);
        }
    }

    public static class PostStubBuilder extends Stub {
        PostStubBuilder(WireMockServer server, String url) {
            super(server, "POST", url);
        }
    }

    public static class PutStubBuilder extends Stub {
        PutStubBuilder(WireMockServer server, String url) {
            super(server, "PUT", url);
        }
    }

    public static class DeleteStubBuilder extends Stub {
        DeleteStubBuilder(WireMockServer server, String url) {
            super(server, "DELETE", url);
        }
    }

    public static class PatchStubBuilder extends Stub {
        PatchStubBuilder(WireMockServer server, String url) {
            super(server, "PATCH", url);
        }
    }
}
