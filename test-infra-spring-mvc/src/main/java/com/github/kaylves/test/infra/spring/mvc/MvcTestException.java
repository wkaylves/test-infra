package com.github.kaylves.test.infra.spring.mvc;

public class MvcTestException extends RuntimeException {

    public MvcTestException(String message, Throwable cause) {
        super(message, cause);
    }
}
