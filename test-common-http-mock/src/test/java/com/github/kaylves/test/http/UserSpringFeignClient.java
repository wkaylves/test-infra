package com.github.kaylves.test.http;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface UserSpringFeignClient {

    // ==================== JSON ====================

    @GetMapping("/api/users/{id}")
    Map<String, Object> getUser(@PathVariable("id") Long id);

    @GetMapping("/api/users")
    List<Map<String, Object>> listUsers();

    @GetMapping("/api/users/search")
    List<Map<String, Object>> searchUsers(@RequestParam("name") String name);

    @PostMapping("/api/users")
    Map<String, Object> createUser(@RequestBody Map<String, Object> user);

    @PutMapping("/api/users/{id}")
    Map<String, Object> updateUser(@PathVariable("id") Long id, @RequestBody Map<String, Object> user);

    @DeleteMapping("/api/users/{id}")
    Map<String, Object> deleteUser(@PathVariable("id") Long id);

    // ==================== Form ====================

    @PostMapping("/api/users/form")
    Map<String, Object> createUserForm(@RequestBody Map<String, Object> form);

    // ==================== XML ====================

    @PostMapping("/api/users/xml")
    String createUserXml(@RequestBody String xmlBody);

    @GetMapping("/api/users/{id}/xml")
    String getUserXml(@PathVariable("id") Long id);

    // ==================== Text ====================

    @PostMapping("/api/users/text")
    String createUserText(@RequestBody String textBody);

    @GetMapping("/api/users/{id}/text")
    String getUserText(@PathVariable("id") Long id);
}
