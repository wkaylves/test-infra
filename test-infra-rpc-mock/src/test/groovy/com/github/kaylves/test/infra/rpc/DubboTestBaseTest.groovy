package com.github.kaylves.test.infra.rpc

import spock.lang.Specification

import static org.mockito.Mockito.when

class DubboTestBaseTest extends Specification {

    interface SampleDubboService {
        String sayHello(String name)
        List<String> listItems()
    }

    def testBase = new DubboTestBase()

    // ==================== mockDubboService ====================

    def "mockDubboService should return non-null mock"() {
        when:
        def service = testBase.mockDubboService(SampleDubboService)

        then:
        service != null
    }

    def "mockDubboService should return mock of correct type"() {
        when:
        def service = testBase.mockDubboService(SampleDubboService)

        then:
        service instanceof SampleDubboService
    }

    def "mockDubboService should allow stubbing"() {
        given:
        def service = testBase.mockDubboService(SampleDubboService)
        when(service.sayHello("World")).thenReturn("Hello World")

        when:
        def result = service.sayHello("World")

        then:
        result == "Hello World"
    }

    def "mockDubboService should return null for unstubbed methods"() {
        given:
        def service = testBase.mockDubboService(SampleDubboService)

        when:
        def result = service.sayHello("World")

        then:
        result == null
    }

    // ==================== stubDubboService ====================

    def "stubDubboService should return non-null mock"() {
        when:
        def service = testBase.stubDubboService(SampleDubboService)

        then:
        service != null
    }

    def "stubDubboService should return mock of correct type"() {
        when:
        def service = testBase.stubDubboService(SampleDubboService)

        then:
        service instanceof SampleDubboService
    }

    def "stubDubboService should allow stubbing"() {
        given:
        def service = testBase.stubDubboService(SampleDubboService)
        when(service.listItems()).thenReturn(["item1", "item2"])

        when:
        def result = service.listItems()

        then:
        result.size() == 2
        result[0] == "item1"
    }
}
