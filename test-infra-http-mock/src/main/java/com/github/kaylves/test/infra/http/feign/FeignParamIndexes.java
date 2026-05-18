package com.github.kaylves.test.infra.http.feign;

import java.util.Map;

public final class FeignParamIndexes {

    private final Map<Integer, String> pathVariables;
    private final Map<Integer, String> queryParams;

    public FeignParamIndexes(Map<Integer, String> pathVariables, Map<Integer, String> queryParams) {
        this.pathVariables = pathVariables;
        this.queryParams = queryParams;
    }

    public Map<Integer, String> pathVariables() {
        return pathVariables;
    }

    public Map<Integer, String> queryParams() {
        return queryParams;
    }
}
