package com.github.kaylves.test.infra.spock

class BaseSpockSpecTest extends BaseSpockSpec {

    def "BaseSpockSpec should extend Specification"() {
        expect:
        this instanceof spock.lang.Specification
    }

    def "mock helper should create GroovyMock"() {
        when:
        def list = mock(List)

        then:
        list != null
        list instanceof List
    }

    def "stub helper should create GroovyStub"() {
        when:
        def list = stub(List)

        then:
        list != null
        list instanceof List
    }

    def "spy helper should create GroovySpy"() {
        given:
        def original = new SampleService()

        when:
        def spied = spy(original)

        then:
        spied != null
        spied instanceof SampleService
    }

    def "spy should preserve original behavior"() {
        given:
        def original = new SampleService()

        when:
        def spied = spy(original)

        then:
        spied.greet("World") == "Hello World"
    }

    static class SampleService {
        String greet(String name) {
            return "Hello " + name
        }
    }

    def "mock helper should support interaction verification"() {
        given:
        def list = mock(List)

        when:
        list.add("item")

        then:
        1 * list.add("item")
    }

    def "stub helper should return default values for unstubbed methods"() {
        given:
        def list = stub(List)

        when:
        def result = list.size()

        then:
        result == 0
    }
}
