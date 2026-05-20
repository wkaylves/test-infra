package com.github.kaylves.test.infra.spring.mvc;

import com.github.kaylves.test.infra.core.matcher.JsonPathMatcher;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;

public class MvcTestResult {

    private final int status;
    private final JsonPathMatcher body;

    public MvcTestResult(int status, JsonPathMatcher body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public JsonPathMatcher getBody() {
        return body;
    }

    public String readString(String path) {
        return body.readString(path);
    }

    public Integer readInt(String path) {
        return body.readInt(path);
    }

    public Long readLong(String path) {
        return body.readLong(path);
    }

    public Boolean readBoolean(String path) {
        return body.readBoolean(path);
    }

    public boolean hasPath(String path) {
        return body.hasPath(path);
    }

    public AbstractIntegerAssert<?> assertStatus() {
        return Assertions.assertThat(status);
    }

    public MvcTestResult assertStatus(int expected) {
        Assertions.assertThat(status)
                .as("Check HTTP status")
                .isEqualTo(expected);
        return this;
    }

    public MvcTestResult assert2xx() {
        Assertions.assertThat(status)
                .as("Check 2xx HTTP status")
                .isBetween(200, 299);
        return this;
    }

    public MvcTestResult assert4xx() {
        Assertions.assertThat(status)
                .as("Check 4xx HTTP status")
                .isBetween(400, 499);
        return this;
    }

    public MvcTestResult assert5xx() {
        Assertions.assertThat(status)
                .as("Check 5xx HTTP status")
                .isBetween(500, 599);
        return this;
    }

    public ObjectAssert<JsonPathMatcher> assertBody() {
        return Assertions.assertThat(body);
    }
}
