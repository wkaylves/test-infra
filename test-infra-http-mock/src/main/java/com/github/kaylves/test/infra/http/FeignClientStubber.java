package com.github.kaylves.test.infra.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaylves.test.infra.http.feign.FeignClientContractStrategy;
import com.github.kaylves.test.infra.http.feign.FeignMethodMeta;
import com.github.kaylves.test.infra.http.feign.NativeFeignContractStrategy;
import com.github.kaylves.test.infra.http.openfeign.SpringCloudOpenFeignContractStrategy;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import feign.Feign;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.EncodeException;
import feign.codec.Encoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FeignClientStubber<T> {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final List<FeignClientContractStrategy> CONTRACT_STRATEGIES = Arrays.asList(
            new NativeFeignContractStrategy(),
            new SpringCloudOpenFeignContractStrategy()
    );

    private final Class<T> clientClass;
    private final WireMockServer server;
    private final Map<Method, FeignMethodMeta> metaMap = new HashMap<>();
    private final Map<String, Method> methodsByName = new HashMap<>();
    private final Set<String> overloadedMethodNames = new HashSet<>();
    private final Map<Method, ResponseSpec> pendingResponses = new ConcurrentHashMap<>();
    private final T realClient;

    public FeignClientStubber(Class<T> clientClass, WireMockServer server) {
        if (clientClass == null) {
            throw new IllegalArgumentException("Feign client class must not be null.");
        }
        if (server == null) {
            throw new IllegalArgumentException("WireMockServer must not be null.");
        }
        this.clientClass = clientClass;
        this.server = server;

        FeignClientContractStrategy selectedStrategy = parseClientMethods(clientClass);
        Feign.Builder builder = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder());
        if (selectedStrategy != null) {
            selectedStrategy.configure(builder);
        }
        this.realClient = builder.target(clientClass, server.baseUrl());
    }

    public T getClient() {
        Object proxy = Proxy.newProxyInstance(
                clientClass.getClassLoader(),
                new Class<?>[]{clientClass},
                new StubRegisteringHandler()
        );
        return clientClass.cast(proxy);
    }

    public ResponseBuilder willReturn(String methodName) {
        if (overloadedMethodNames.contains(methodName)) {
            throw new IllegalArgumentException("Overloaded Feign method name '" + methodName + "' is ambiguous on "
                    + clientClass.getName() + ". Use willReturn(Class, Method).");
        }
        Method method = methodsByName.get(methodName);
        if (method == null) {
            throw new IllegalArgumentException("No supported Feign method named '" + methodName + "' found on " + clientClass.getName() + ".");
        }
        return willReturn(method);
    }

    public ResponseBuilder willReturn(Method method) {
        if (!metaMap.containsKey(method)) {
            throw new IllegalArgumentException("Unsupported Feign method: " + method.toGenericString());
        }
        return new ResponseBuilder(method);
    }

    private FeignClientContractStrategy parseClientMethods(Class<T> clientClass) {
        FeignClientContractStrategy selectedStrategy = null;
        for (Method method : clientClass.getMethods()) {
            if (method.getDeclaringClass() == Object.class || method.isDefault()) {
                continue;
            }
            ParsedMethod parsedMethod = parseMethod(clientClass, method);
            if (parsedMethod == null) {
                continue;
            }
            if (selectedStrategy != null && selectedStrategy != parsedMethod.strategy) {
                throw new IllegalArgumentException("Do not mix native Feign and Spring Cloud OpenFeign annotations on "
                        + clientClass.getName() + ".");
            }
            selectedStrategy = parsedMethod.strategy;
            putMethodMeta(method, parsedMethod.meta);
        }
        return selectedStrategy;
    }

    private ParsedMethod parseMethod(Class<?> clientClass, Method method) {
        for (FeignClientContractStrategy strategy : CONTRACT_STRATEGIES) {
            FeignMethodMeta meta = strategy.parse(clientClass, method);
            if (meta != null) {
                return new ParsedMethod(strategy, meta);
            }
        }
        return null;
    }

    private void putMethodMeta(Method method, FeignMethodMeta meta) {
        Method previous = methodsByName.put(method.getName(), method);
        if (previous != null) {
            overloadedMethodNames.add(method.getName());
        }
        metaMap.put(method, meta);
    }

    private class StubRegisteringHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return invokeObjectMethod(proxy, method, args);
            }
            FeignMethodMeta meta = metaMap.get(method);
            if (meta == null) {
                return invokeReal(method, args);
            }

            ResponseSpec pending = pendingResponses.remove(method);
            if (pending != null) {
                registerStub(meta, args, pending);
            }

            return invokeReal(method, args);
        }

        private Object invokeObjectMethod(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            if ("toString".equals(name)) {
                return clientClass.getName() + " WireMock stub proxy";
            }
            if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            }
            if ("equals".equals(name)) {
                return proxy == args[0];
            }
            throw new UnsupportedOperationException("Unsupported Object method: " + name);
        }

        private Object invokeReal(Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(realClient, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    private void registerStub(FeignMethodMeta meta, Object[] args, ResponseSpec responseSpec) {
        String url = resolveUrl(meta, args);
        MappingBuilder mappingBuilder;
        switch (meta.httpMethod()) {
            case "POST":
                mappingBuilder = WireMock.post(WireMock.urlEqualTo(url));
                break;
            case "PUT":
                mappingBuilder = WireMock.put(WireMock.urlEqualTo(url));
                break;
            case "DELETE":
                mappingBuilder = WireMock.delete(WireMock.urlEqualTo(url));
                break;
            case "PATCH":
                mappingBuilder = WireMock.patch(WireMock.urlEqualTo(url));
                break;
            default:
                mappingBuilder = WireMock.get(WireMock.urlEqualTo(url));
        }
        server.stubFor(mappingBuilder
                .willReturn(WireMock.aResponse()
                        .withStatus(responseSpec.status)
                        .withHeader("Content-Type", responseSpec.contentType)
                        .withBody(responseSpec.body)));
    }

    private String resolveUrl(FeignMethodMeta meta, Object[] args) {
        String url = meta.urlTemplate();
        for (Map.Entry<Integer, String> entry : meta.pathVariables().entrySet()) {
            int idx = entry.getKey();
            if (args == null || idx >= args.length) {
                continue;
            }
            url = url.replace("{" + entry.getValue() + "}", encode(args[idx]));
        }
        if (!meta.queryParams().isEmpty()) {
            List<String> query = new ArrayList<>();
            for (Map.Entry<Integer, String> entry : meta.queryParams().entrySet()) {
                int idx = entry.getKey();
                if (args == null || idx >= args.length || args[idx] == null) {
                    continue;
                }
                query.add(entry.getValue() + "=" + encode(args[idx]));
            }
            if (!query.isEmpty()) {
                url = url + (url.contains("?") ? "&" : "?") + joinQuery(query);
            }
        }
        return url;
    }

    private String joinQuery(List<String> query) {
        StringBuilder result = new StringBuilder();
        for (String item : query) {
            if (result.length() > 0) {
                result.append('&');
            }
            result.append(item);
        }
        return result.toString();
    }

    private String encode(Object value) {
        try {
            return URLEncoder.encode(String.valueOf(value), "UTF-8");
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to URL encode value: " + value, e);
        }
    }

    public class ResponseBuilder {
        private final Method method;

        ResponseBuilder(Method method) {
            this.method = method;
        }

        public FeignClientStubber<T> response(Object body) {
            pendingResponses.put(method, ResponseSpec.of(200, body));
            return FeignClientStubber.this;
        }

        public FeignClientStubber<T> response(int status, Object body) {
            pendingResponses.put(method, ResponseSpec.of(status, body));
            return FeignClientStubber.this;
        }

        public FeignClientStubber<T> json(Object body) {
            pendingResponses.put(method, ResponseSpec.of(200, body, WireMockStubBuilder.APPLICATION_JSON));
            return FeignClientStubber.this;
        }

        public FeignClientStubber<T> text(String body) {
            pendingResponses.put(method, ResponseSpec.of(200, body, WireMockStubBuilder.TEXT_PLAIN));
            return FeignClientStubber.this;
        }

        public FeignClientStubber<T> xml(String body) {
            pendingResponses.put(method, ResponseSpec.of(200, body, WireMockStubBuilder.APPLICATION_XML));
            return FeignClientStubber.this;
        }
    }

    private static class ParsedMethod {
        final FeignClientContractStrategy strategy;
        final FeignMethodMeta meta;

        ParsedMethod(FeignClientContractStrategy strategy, FeignMethodMeta meta) {
            this.strategy = strategy;
            this.meta = meta;
        }
    }

    private static class ResponseSpec {
        final int status;
        final String body;
        final String contentType;

        ResponseSpec(int status, String body, String contentType) {
            if (status < 100 || status > 599) {
                throw new IllegalArgumentException("HTTP status must be between 100 and 599.");
            }
            this.status = status;
            this.body = body;
            this.contentType = contentType;
        }

        static ResponseSpec of(int status, Object body) {
            return of(status, body, WireMockStubBuilder.APPLICATION_JSON);
        }

        static ResponseSpec of(int status, Object body, String contentType) {
            try {
                String responseBody;
                if (body == null) {
                    responseBody = "{}";
                } else if (body instanceof String) {
                    responseBody = (String) body;
                } else {
                    responseBody = MAPPER.writeValueAsString(body);
                }
                return new ResponseSpec(status, responseBody, contentType);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to serialize Feign stub response.", e);
            }
        }
    }

    private static class JacksonEncoder implements Encoder {
        @Override
        public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
            try {
                if (object instanceof String) {
                    template.body((String) object);
                } else {
                    template.body(MAPPER.writeValueAsString(object));
                }
            } catch (Exception e) {
                throw new EncodeException(e.getMessage(), e);
            }
        }
    }

    private static class JacksonDecoder implements Decoder {
        @Override
        public Object decode(Response response, Type type) throws DecodeException, feign.FeignException {
            try {
                byte[] bytes = feign.Util.toByteArray(response.body().asInputStream());
                String body = new String(bytes, StandardCharsets.UTF_8);
                if (type == String.class || type == Object.class) {
                    return body;
                }
                return MAPPER.readValue(body, MAPPER.constructType(type));
            } catch (feign.FeignException e) {
                throw e;
            } catch (Exception e) {
                throw new DecodeException(response.status(), e.getMessage(), response.request(), e);
            }
        }
    }
}
