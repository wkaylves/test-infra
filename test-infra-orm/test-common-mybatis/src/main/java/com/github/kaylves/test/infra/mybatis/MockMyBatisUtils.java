package com.github.kaylves.test.infra.mybatis;

import org.mockito.Mockito;

public class MockMyBatisUtils {

    private MockMyBatisUtils() {
    }

    public static <T> T mockMapper(Class<T> mapperClass) {
        return Mockito.mock(mapperClass);
    }
}
