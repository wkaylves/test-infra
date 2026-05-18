package com.github.kaylves.test.infra.http.feign;

import com.github.kaylves.test.infra.http.UserFeignClient;
import com.github.kaylves.test.infra.http.UserSpringFeignClient;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class NativeFeignContractStrategyTest {

    private final NativeFeignContractStrategy strategy = new NativeFeignContractStrategy();

    @Test
    void parsesNativeFeignRequestLine() throws Exception {
        Method method = UserFeignClient.class.getMethod("getUser", Long.class);

        FeignMethodMeta meta = strategy.parse(UserFeignClient.class, method);

        assertThat(meta.httpMethod()).isEqualTo("GET");
        assertThat(meta.urlTemplate()).isEqualTo("/api/users/{id}");
        assertThat(meta.pathVariables()).containsEntry(0, "id");
    }

    @Test
    void ignoresSpringCloudOpenFeignMethod() throws Exception {
        Method method = UserSpringFeignClient.class.getMethod("getUser", Long.class);

        assertThat(strategy.parse(UserSpringFeignClient.class, method)).isNull();
    }
}
