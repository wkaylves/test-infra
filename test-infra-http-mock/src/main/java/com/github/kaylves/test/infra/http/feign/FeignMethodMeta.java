package com.github.kaylves.test.infra.http.feign;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FeignMethodMeta {

    private final Method method;
    private final String httpMethod;
    private final String urlTemplate;
    private final Map<Integer, String> pathVariables;
    private final Map<Integer, String> queryParams;

    public FeignMethodMeta(Method method, String httpMethod, String urlTemplate,
                           Map<Integer, String> pathVariables, Map<Integer, String> queryParams) {
        this.method = method;
        this.httpMethod = httpMethod;
        this.urlTemplate = urlTemplate;
        this.pathVariables = Collections.unmodifiableMap(new LinkedHashMap<>(pathVariables));
        this.queryParams = Collections.unmodifiableMap(new LinkedHashMap<>(queryParams));
    }

    public Method method() {
        return method;
    }

    public String httpMethod() {
        return httpMethod;
    }

    public String urlTemplate() {
        return urlTemplate;
    }

    public Map<Integer, String> pathVariables() {
        return pathVariables;
    }

    public Map<Integer, String> queryParams() {
        return queryParams;
    }
}
