package com.github.kaylves.test.infra.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeignClientStubberContractTest {

    @Test
    void overloadedMethodNameStubbingFailsFast() {
        WireMockServer server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();
        try {
            FeignClientStubber<OverloadedFeignClient> stubber = new FeignClientStubber<>(OverloadedFeignClient.class, server);

            assertThatThrownBy(() -> stubber.willReturn("getUser"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Overloaded Feign method name");
        } finally {
            server.stop();
        }
    }

    @Test
    void overloadedMethodObjectStubbingIsAllowed() throws Exception {
        WireMockServer server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();
        try {
            FeignClientStubber<OverloadedFeignClient> stubber = new FeignClientStubber<>(OverloadedFeignClient.class, server);
            Method method = OverloadedFeignClient.class.getMethod("getUser", Long.class);

            stubber.willReturn(method).response("{}");
        } finally {
            server.stop();
        }
    }
}
