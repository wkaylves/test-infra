package com.github.kaylves.test.infra.spring.mvc;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
class TestController {

    @GetMapping("/hello")
    public ResponseEntity<Map<String, Object>> hello() {
        Map<String, Object> m = new HashMap<>();
        m.put("message", "hello");
        return ResponseEntity.ok(m);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestParam String keyword) {
        Map<String, Object> m = new HashMap<>();
        m.put("keyword", keyword);
        return ResponseEntity.ok(m);
    }

    @GetMapping("/header")
    public ResponseEntity<Map<String, Object>> echoHeader(@RequestHeader("X-Custom") String custom) {
        Map<String, Object> m = new HashMap<>();
        m.put("customHeader", custom);
        return ResponseEntity.ok(m);
    }

    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(body);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> update(@RequestBody Map<String, Object> body) {
        body.put("updated", true);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/partial")
    public ResponseEntity<Map<String, Object>> partial(@RequestBody Map<String, Object> body) {
        body.put("patched", true);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> remove() {
        Map<String, Object> m = new HashMap<>();
        m.put("deleted", true);
        return ResponseEntity.ok(m);
    }

    @DeleteMapping("/remove-with-body")
    public ResponseEntity<Map<String, Object>> removeWithBody(@RequestBody Map<String, Object> body) {
        body.put("deleted", true);
        return ResponseEntity.ok(body);
    }
}
