package com.github.kaylves.test.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseWireMock {

    protected static WireMockServer wireMockServer;

    private static final Map<Class<?>, FeignClientStubber<?>> HELPER_CACHE = new ConcurrentHashMap<>();

    protected static void startWireMock() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort().notifier(new Slf4jNotifier(true)));
        wireMockServer.start();
    }

    protected static void stopWireMock() {
        HELPER_CACHE.clear();
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    protected String getBaseUrl() {
        return wireMockServer.baseUrl();
    }

    protected int getPort() {
        return wireMockServer.port();
    }

    // ==================== Feign ====================

    @SuppressWarnings("unchecked")
    protected static <T> FeignClientStubber<T> feignHelper(Class<T> clientClass) {
        return (FeignClientStubber<T>) HELPER_CACHE.computeIfAbsent(
                clientClass, c -> new FeignClientStubber<>(clientClass, wireMockServer));
    }

    protected static <T> T feignClient(Class<T> clientClass) {
        return feignHelper(clientClass).getClient();
    }

    protected static FeignClientStubber.ResponseBuilder willReturn(Class<?> clientClass, String methodName) {
        return feignHelper(clientClass).willReturn(methodName);
    }

    // ==================== stub 快捷方法 ====================

    protected static StubDef stubGet(String url) {
        return new StubDef(wireMockServer, url, "GET");
    }

    protected static StubDef stubPost(String url) {
        return new StubDef(wireMockServer, url, "POST");
    }

    protected static StubDef stubPut(String url) {
        return new StubDef(wireMockServer, url, "PUT");
    }

    protected static StubDef stubDelete(String url) {
        return new StubDef(wireMockServer, url, "DELETE");
    }

    protected static StubDef stubPatch(String url) {
        return new StubDef(wireMockServer, url, "PATCH");
    }

    // ==================== 数据构造 ====================

    protected static Map<String, Object> user(Integer id, String name, String email) {
        Map<String, Object> m = new HashMap<>();
        if (id != null) m.put("id", id);
        if (name != null) m.put("name", name);
        if (email != null) m.put("email", email);
        return m;
    }

    protected static Map<String, Object> error(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("error", msg);
        return m;
    }

    protected static Map<String, Object> message(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("message", msg);
        return m;
    }

    protected static Map<String, Object> map(String key, Object value) {
        Map<String, Object> m = new HashMap<>();
        m.put(key, value);
        return m;
    }

    // ==================== 断言 ====================

    protected static void assertField(Map<String, Object> data, String field, Object expected) {
        assertThat(data.get(field)).isEqualTo(expected);
    }

    protected static void assertListSize(List<?> list, int expectedSize) {
        assertThat(list).hasSize(expectedSize);
    }
}