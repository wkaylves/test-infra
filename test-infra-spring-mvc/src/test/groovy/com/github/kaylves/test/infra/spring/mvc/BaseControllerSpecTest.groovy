package com.github.kaylves.test.infra.spring.mvc

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(TestController)
class BaseControllerSpecTest extends BaseControllerSpec {

    def "performGet should return correct JSON and status"() {
        when:
        def result = performGet("/api/test/hello")

        then:
        result.assert2xx().readString('$.message') == 'hello'
    }

    def "performGet with headers should pass headers to request"() {
        given:
        def headers = ['X-Custom': 'my-value']

        when:
        def result = performGet("/api/test/header", headers)

        then:
        result.assert2xx().readString('$.customHeader') == 'my-value'
    }

    def "performGet with query params should pass params to request"() {
        given:
        def queryParams = [keyword: 'java']

        when:
        def result = performGet("/api/test/search", null, queryParams)

        then:
        result.readString('$.keyword') == 'java'
    }

    def "performGet with headers and query params"() {
        given:
        def headers = ['X-Custom': 'combined']
        def queryParams = [keyword: 'test']

        when:
        def result = performGet("/api/test/search", headers, queryParams)

        then:
        result.readString('$.keyword') == 'test'
    }

    def "performPost should echo request body"() {
        given:
        def body = '{"name":"test"}'

        when:
        def result = performPost("/api/test/echo", body)

        then:
        result.assert2xx().readString('$.name') == 'test'
    }

    def "performPut should update and return"() {
        given:
        def body = '{"name":"updated"}'

        when:
        def result = performPut("/api/test/update", body)

        then:
        result.readString('$.name') == 'updated'
        result.readBoolean('$.updated') == true
    }

    def "performPatch should partial update"() {
        given:
        def body = '{"field":"value"}'

        when:
        def result = performPatch("/api/test/partial", body)

        then:
        result.readString('$.field') == 'value'
        result.readBoolean('$.patched') == true
    }

    def "performDelete should return deleted flag"() {
        when:
        def result = performDelete("/api/test/remove")

        then:
        result.readBoolean('$.deleted') == true
    }

    def "performDeleteWithBody should send body with delete request"() {
        given:
        def body = '{"id":1}'

        when:
        def result = performDeleteWithBody("/api/test/remove-with-body", body)

        then:
        result.readBoolean('$.deleted') == true
        result.readInt('$.id') == 1
    }

    def "assertStatus should verify HTTP status"() {
        when:
        def result = performGet("/api/test/hello")

        then:
        result.assertStatus(200).is(result)
    }

    def "assertStatus should throw on mismatch"() {
        given:
        def result = performGet("/api/test/hello")

        when:
        result.assertStatus(404)

        then:
        def ex = thrown(AssertionError)
        ex.message.contains('Check HTTP status')
    }

    def "assert2xx should verify 2xx status"() {
        when:
        def result = performGet("/api/test/hello")

        then:
        result.assert2xx().is(result)
    }

    def "assertStatus() should return AssertJ IntegerAssert for fluent chaining"() {
        when:
        def result = performGet("/api/test/hello")

        then:
        result.assertStatus().isEqualTo(200)
        result.assertStatus().isBetween(100, 300)
    }

    def "assertBody() should return AssertJ ObjectAssert for JsonPathMatcher"() {
        when:
        def result = performGet("/api/test/hello")

        then:
        result.assertBody() != null
    }

    def "MvcTestResult body delegation should work"() {
        when:
        def result = performGet("/api/test/hello")

        then:
        result.hasPath('$.message')
        !result.hasPath('$.nonexistent')
    }

    def "MvcTestResult getBody should return JsonPathMatcher"() {
        when:
        def result = performGet("/api/test/hello")

        then:
        result.body != null
        result.body.readString('$.message') == 'hello'
    }
}
