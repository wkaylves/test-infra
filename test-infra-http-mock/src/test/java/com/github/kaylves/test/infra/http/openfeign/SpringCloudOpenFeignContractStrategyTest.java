package com.github.kaylves.test.infra.http.openfeign;

import com.github.kaylves.test.infra.http.UserFeignClient;
import com.github.kaylves.test.infra.http.UserSpringFeignClient;
import com.github.kaylves.test.infra.http.feign.FeignMethodMeta;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class SpringCloudOpenFeignContractStrategyTest {

    private final SpringCloudOpenFeignContractStrategy strategy = new SpringCloudOpenFeignContractStrategy();

    @Test
    void parsesSpringCloudOpenFeignRequestParam() throws Exception {
        Method method = UserSpringFeignClient.class.getMethod("searchUsers", String.class);

        FeignMethodMeta meta = strategy.parse(UserSpringFeignClient.class, method);

        assertThat(meta.httpMethod()).isEqualTo("GET");
        assertThat(meta.urlTemplate()).isEqualTo("/api/users/search");
        assertThat(meta.queryParams()).containsEntry(0, "name");
    }

    @Test
    void ignoresNativeFeignMethod() throws Exception {
        Method method = UserFeignClient.class.getMethod("getUser", Long.class);

        assertThat(strategy.parse(UserFeignClient.class, method)).isNull();
    }
}
