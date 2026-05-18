package com.github.kaylves.test.infra.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public abstract class BaseWireMock {

    protected WireMockServer wireMockServer;

    private final Map<Class<?>, FeignClientStubber<?>> helperCache = new ConcurrentHashMap<>();

    protected void startWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            return;
        }
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort().notifier(new Slf4jNotifier(true)));
        wireMockServer.start();
    }

    protected void stopWireMock() {
        helperCache.clear();
        if (wireMockServer != null) {
            wireMockServer.stop();
            wireMockServer = null;
        }
    }

    protected String getBaseUrl() {
        return getWireMockServer().baseUrl();
    }

    protected int getPort() {
        return getWireMockServer().port();
    }

    protected WireMockServer getWireMockServer() {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            throw new IllegalStateException("WireMock server is not running. Call startWireMock() first or use a framework-specific base class.");
        }
        return wireMockServer;
    }

    protected void resetWireMock() {
        getWireMockServer().resetAll();
    }

    // ==================== Feign ====================

    @SuppressWarnings("unchecked")
    protected <T> FeignClientStubber<T> feignHelper(Class<T> clientClass) {
        return (FeignClientStubber<T>) helperCache.computeIfAbsent(
                clientClass, c -> new FeignClientStubber<>(clientClass, getWireMockServer()));
    }

    protected <T> T feignClient(Class<T> clientClass) {
        return feignHelper(clientClass).getClient();
    }

    protected FeignClientStubber.ResponseBuilder willReturn(Class<?> clientClass, String methodName) {
        return feignHelper(clientClass).willReturn(methodName);
    }

    protected FeignClientStubber.ResponseBuilder willReturn(Class<?> clientClass, Method method) {
        return feignHelper(clientClass).willReturn(method);
    }

    // ==================== stub 快捷方法 ====================

    protected WireMockStubBuilder wireMock() {
        return WireMockStubBuilder.on(getWireMockServer());
    }

    protected StubDef stubGet(String url) {
        return new StubDef(getWireMockServer(), url, "GET");
    }

    protected StubDef stubPost(String url) {
        return new StubDef(getWireMockServer(), url, "POST");
    }

    protected StubDef stubPut(String url) {
        return new StubDef(getWireMockServer(), url, "PUT");
    }

    protected StubDef stubDelete(String url) {
        return new StubDef(getWireMockServer(), url, "DELETE");
    }

    protected StubDef stubPatch(String url) {
        return new StubDef(getWireMockServer(), url, "PATCH");
    }
}
