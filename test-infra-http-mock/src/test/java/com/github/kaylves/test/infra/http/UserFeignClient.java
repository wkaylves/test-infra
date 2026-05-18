package com.github.kaylves.test.infra.http;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface UserFeignClient {

    // ==================== JSON ====================

    @RequestLine("GET /api/users/{id}")
    Map<String, Object> getUser(@Param("id") Long id);

    @RequestLine("GET /api/users")
    List<Map<String, Object>> listUsers();

    @RequestLine("POST /api/users")
    @Headers("Content-Type: application/json")
    Map<String, Object> createUser(Map<String, Object> user);

    @RequestLine("PUT /api/users/{id}")
    @Headers("Content-Type: application/json")
    Map<String, Object> updateUser(@Param("id") Long id, Map<String, Object> user);

    @RequestLine("DELETE /api/users/{id}")
    Map<String, Object> deleteUser(@Param("id") Long id);

    // ==================== Form ====================

    @RequestLine("POST /api/users/form")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Map<String, Object> createUserForm(Map<String, Object> form);

    // ==================== XML ====================

    @RequestLine("POST /api/users/xml")
    @Headers("Content-Type: application/xml")
    String createUserXml(String xmlBody);

    @RequestLine("GET /api/users/{id}/xml")
    @Headers("Accept: application/xml")
    String getUserXml(@Param("id") Long id);

    // ==================== Text ====================

    @RequestLine("POST /api/users/text")
    @Headers("Content-Type: text/plain")
    String createUserText(String textBody);

    @RequestLine("GET /api/users/{id}/text")
    @Headers("Accept: text/plain")
    String getUserText(@Param("id") Long id);
}