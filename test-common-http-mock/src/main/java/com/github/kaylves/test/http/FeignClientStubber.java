package com.github.kaylves.test.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.EncodeException;
import feign.codec.Encoder;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class FeignClientStubber<T> {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String[][] MVC_MAPPINGS = {
            {"org.springframework.web.bind.annotation.GetMapping", "GET"},
            {"org.springframework.web.bind.annotation.PostMapping", "POST"},
            {"org.springframework.web.bind.annotation.PutMapping", "PUT"},
            {"org.springframework.web.bind.annotation.DeleteMapping", "DELETE"},
            {"org.springframework.web.bind.annotation.PatchMapping", "PATCH"},
    };

    private final Class<T> clientClass;
    private final WireMockServer server;
    private final Map<String, MethodMeta> metaMap = new HashMap<>();
    private final Map<String, ResponseSpec> pendingResponses = new HashMap<>();
    private final T realClient;

    public FeignClientStubber(Class<T> clientClass, WireMockServer server) {
        this.clientClass = clientClass;
        this.server = server;

        boolean hasMvcAnnotations = false;

        for (Method m : clientClass.getMethods()) {
            // Try raw Feign @RequestLine first
            RequestLine rl = m.getAnnotation(RequestLine.class);
            if (rl != null) {
                String[] parts = rl.value().split("\\s+", 2);
                Map<Integer, String> paramIndex = new HashMap<>();
                Annotation[][] paramAnnotations = m.getParameterAnnotations();
                for (int i = 0; i < paramAnnotations.length; i++) {
                    for (Annotation ann : paramAnnotations[i]) {
                        if (ann instanceof Param) {
                            paramIndex.put(i, ((Param) ann).value());
                            break;
                        }
                    }
                }
                metaMap.put(m.getName(), new MethodMeta(m, parts[0], parts[1], paramIndex));
                continue;
            }

            // Try Spring MVC annotations via reflection
            MethodMeta mvcMeta = parseSpringMvcMethod(m);
            if (mvcMeta != null) {
                metaMap.put(m.getName(), mvcMeta);
                hasMvcAnnotations = true;
            }
        }

        feign.Feign.Builder builder = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder());

        if (hasMvcAnnotations) {
            try {
                Class<?> contractClass = Class.forName("org.springframework.cloud.openfeign.support.SpringMvcContract");
                builder.contract((feign.Contract) contractClass.newInstance());
            } catch (Exception e) {
                // SpringMvcContract not available, fall back to default
            }
        }

        this.realClient = builder.target(clientClass, server.baseUrl());
    }

    private MethodMeta parseSpringMvcMethod(Method m) {
        for (String[] mapping : MVC_MAPPINGS) {
            Annotation ann = findAnnotation(m, mapping[0]);
            if (ann != null) {
                String url = extractAnnotationValue(ann);
                if (url == null) continue;

                String httpMethod = mapping[1];
                Map<Integer, String> paramIndex = new HashMap<>();
                Annotation[][] paramAnnotations = m.getParameterAnnotations();
                for (int i = 0; i < paramAnnotations.length; i++) {
                    for (Annotation pa : paramAnnotations[i]) {
                        if (pa.annotationType().getName().equals("org.springframework.web.bind.annotation.PathVariable")) {
                            String name = extractAnnotationValue(pa);
                            if (name != null) {
                                paramIndex.put(i, name);
                            }
                            break;
                        }
                    }
                }
                return new MethodMeta(m, httpMethod, url, paramIndex);
            }
        }
        return null;
    }

    private Annotation findAnnotation(Method m, String annotationClassName) {
        for (Annotation ann : m.getAnnotations()) {
            if (ann.annotationType().getName().equals(annotationClassName)) {
                return ann;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String extractAnnotationValue(Annotation ann) {
        try {
            // Try "value" first, then "path"
            try {
                Object val = ann.annotationType().getMethod("value").invoke(ann);
                if (val instanceof String[]) {
                    String[] arr = (String[]) val;
                    return arr.length > 0 ? arr[0] : null;
                }
                return (String) val;
            } catch (NoSuchMethodException e) {
                Object val = ann.annotationType().getMethod("path").invoke(ann);
                if (val instanceof String[]) {
                    String[] arr = (String[]) val;
                    return arr.length > 0 ? arr[0] : null;
                }
                return (String) val;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public T getClient() {
        return (T) Proxy.newProxyInstance(
                clientClass.getClassLoader(),
                new Class<?>[]{clientClass},
                new StubRegisteringHandler()
        );
    }

    public ResponseBuilder willReturn(String methodName) {
        return new ResponseBuilder(methodName);
    }

    private class StubRegisteringHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            MethodMeta meta = metaMap.get(name);
            if (meta == null) {
                return invokeReal(method, args);
            }

            ResponseSpec pending = pendingResponses.remove(name);
            if (pending != null) {
                String body;
                if (pending.body == null) {
                    body = "{}";
                } else if (pending.body instanceof String) {
                    body = (String) pending.body;
                } else {
                    body = MAPPER.writeValueAsString(pending.body);
                }
                registerStub(meta, args, pending.status, body);
            }

            return invokeReal(method, args);
        }

        private Object invokeReal(Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(realClient, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    private void registerStub(MethodMeta meta, Object[] args, int status, String body) {
        String url = resolveUrl(meta.urlTemplate, meta.paramIndex, args);

        MappingBuilder mappingBuilder;
        switch (meta.httpMethod) {
            case "POST":
                mappingBuilder = WireMock.post(url);
                break;
            case "PUT":
                mappingBuilder = WireMock.put(url);
                break;
            case "DELETE":
                mappingBuilder = WireMock.delete(url);
                break;
            case "PATCH":
                mappingBuilder = WireMock.patch(WireMock.urlEqualTo(url));
                break;
            default:
                mappingBuilder = WireMock.get(url);
        }
        server.stubFor(mappingBuilder
                .willReturn(WireMock.aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
    }

    private String resolveUrl(String template, Map<Integer, String> paramIndex, Object[] args) {
        if (args == null || args.length == 0) return template;
        String url = template;
        for (Map.Entry<Integer, String> entry : paramIndex.entrySet()) {
            int idx = entry.getKey();
            String name = entry.getValue();
            if (idx < args.length) {
                url = url.replace("{" + name + "}", String.valueOf(args[idx]));
            }
        }
        return url;
    }

    private static class MethodMeta {
        final Method method;
        final String httpMethod;
        final String urlTemplate;
        final Map<Integer, String> paramIndex;

        MethodMeta(Method method, String httpMethod, String urlTemplate, Map<Integer, String> paramIndex) {
            this.method = method;
            this.httpMethod = httpMethod;
            this.urlTemplate = urlTemplate;
            this.paramIndex = paramIndex;
        }
    }

    public class ResponseBuilder {
        private final String methodName;

        ResponseBuilder(String methodName) {
            this.methodName = methodName;
        }

        public FeignClientStubber<T> response(Object body) {
            pendingResponses.put(methodName, new ResponseSpec(200, body));
            return FeignClientStubber.this;
        }

        public FeignClientStubber<T> response(int status, Object body) {
            pendingResponses.put(methodName, new ResponseSpec(status, body));
            return FeignClientStubber.this;
        }
    }

    private static class ResponseSpec {
        final int status;
        final Object body;

        ResponseSpec(int status, Object body) {
            this.status = status;
            this.body = body;
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
                String body = new String(bytes, "UTF-8");
                if (type == String.class || type == Object.class) return body;
                return MAPPER.readValue(body, MAPPER.constructType(type));
            } catch (feign.FeignException e) {
                throw e;
            } catch (Exception e) {
                throw new DecodeException(response.status(), e.getMessage(), response.request(), e);
            }
        }
    }
}