package com.github.kaylves.test.http;

import feign.Param;
import feign.RequestLine;

import java.util.Map;

public interface OverloadedFeignClient {

    @RequestLine("GET /api/users/{id}")
    Map<String, Object> getUser(@Param("id") Long id);

    @RequestLine("GET /api/users/by-name/{name}")
    Map<String, Object> getUser(@Param("name") String name);
}
