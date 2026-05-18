package com.github.kaylves.test.infra.core.builder;

public class ResultBuilder<T> {

    private int code = 200;
    private String message = "success";
    private T data;

    public ResultBuilder<T> code(int code) {
        this.code = code;
        return this;
    }

    public ResultBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    public ResultBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    public Result<T> build() {
        return new Result<>(code, message, data);
    }

    public static <T> ResultBuilder<T> builder() {
        return new ResultBuilder<>();
    }

    public static <T> Result<T> ok() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }
}
