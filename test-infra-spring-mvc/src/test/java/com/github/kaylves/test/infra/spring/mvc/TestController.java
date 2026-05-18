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

    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(body);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> update(@RequestBody Map<String, Object> body) {
        body.put("updated", true);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> remove() {
        Map<String, Object> m = new HashMap<>();
        m.put("deleted", true);
        return ResponseEntity.ok(m);
    }
}
