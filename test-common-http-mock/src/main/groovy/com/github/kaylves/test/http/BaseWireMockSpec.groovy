package com.github.kaylves.test.http

import spock.lang.Shared
import spock.lang.Specification

abstract class BaseWireMockSpec extends Specification {

    @Shared
    private BaseWireMock delegate = new BaseWireMock() {}

    def setupSpec() {
        delegate.startWireMock()
    }

    def cleanupSpec() {
        delegate.stopWireMock()
    }

    protected String getBaseUrl() {
        delegate.getBaseUrl()
    }

    protected int getPort() {
        delegate.getPort()
    }

    // ==================== Feign ====================

    protected <T> FeignClientStubber<T> feignHelper(Class<T> clientClass) {
        delegate.feignHelper(clientClass)
    }

    protected <T> T feignClient(Class<T> clientClass) {
        delegate.feignClient(clientClass)
    }

    protected FeignClientStubber.ResponseBuilder willReturn(Class<?> clientClass, String methodName) {
        delegate.willReturn(clientClass, methodName)
    }

    // ==================== stub 快捷方法 ====================

    protected StubDef stubGet(String url) {
        delegate.stubGet(url)
    }

    protected StubDef stubPost(String url) {
        delegate.stubPost(url)
    }

    protected StubDef stubPut(String url) {
        delegate.stubPut(url)
    }

    protected StubDef stubDelete(String url) {
        delegate.stubDelete(url)
    }

    protected StubDef stubPatch(String url) {
        delegate.stubPatch(url)
    }

    // ==================== 数据构造 ====================

    protected Map<String, Object> user(Integer id, String name, String email) {
        delegate.user(id, name, email)
    }

    protected Map<String, Object> error(String msg) {
        delegate.error(msg)
    }

    protected Map<String, Object> message(String msg) {
        delegate.message(msg)
    }

    protected Map<String, Object> map(String key, Object value) {
        delegate.map(key, value)
    }

    // ==================== 断言 ====================

    protected void assertField(Map<String, Object> data, String field, Object expected) {
        delegate.assertField(data, field, expected)
    }

    protected void assertListSize(List<?> list, int expectedSize) {
        delegate.assertListSize(list, expectedSize)
    }
}