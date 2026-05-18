package com.github.kaylves.test.infra.http.feign;

import feign.Param;
import feign.RequestLine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public final class NativeFeignContractStrategy implements FeignClientContractStrategy {

    @Override
    public String name() {
        return "native-feign";
    }

    @Override
    public FeignMethodMeta parse(Class<?> clientClass, Method method) {
        RequestLine requestLine = method.getAnnotation(RequestLine.class);
        if (requestLine == null) {
            return null;
        }
        String[] parts = requestLine.value().trim().split("\\s+", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("@RequestLine must contain HTTP method and URL: " + method.toGenericString());
        }

        FeignParamIndexes indexes = parseFeignParams(method);
        return new FeignMethodMeta(method, parts[0].toUpperCase(), parts[1],
                indexes.pathVariables(), indexes.queryParams());
    }

    private FeignParamIndexes parseFeignParams(Method method) {
        Map<Integer, String> pathVariables = new LinkedHashMap<>();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof Param) {
                    pathVariables.put(i, ((Param) annotation).value());
                    break;
                }
            }
        }
        return new FeignParamIndexes(pathVariables, new LinkedHashMap<Integer, String>());
    }
}
