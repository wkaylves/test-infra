package com.github.kaylves.test.infra.http.feign;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FeignParamIndexes {

    private final Map<Integer, String> pathVariables;
    private final Map<Integer, String> queryParams;

    public FeignParamIndexes(Map<Integer, String> pathVariables, Map<Integer, String> queryParams) {
        this.pathVariables = Collections.unmodifiableMap(new LinkedHashMap<>(pathVariables));
        this.queryParams = Collections.unmodifiableMap(new LinkedHashMap<>(queryParams));
    }

    public Map<Integer, String> pathVariables() {
        return pathVariables;
    }

    public Map<Integer, String> queryParams() {
        return queryParams;
    }
}
