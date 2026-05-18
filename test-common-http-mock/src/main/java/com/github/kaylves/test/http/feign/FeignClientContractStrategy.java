package com.github.kaylves.test.http.feign;

import feign.Feign;

import java.lang.reflect.Method;

public interface FeignClientContractStrategy {

    String name();

    FeignMethodMeta parse(Class<?> clientClass, Method method);

    default void configure(Feign.Builder builder) {
    }
}
