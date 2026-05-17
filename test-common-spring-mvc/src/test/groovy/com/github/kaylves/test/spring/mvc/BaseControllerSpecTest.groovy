package com.github.kaylves.test.spring.mvc

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(TestController)
class BaseControllerSpecTest extends BaseControllerSpec {

    def "performGetAndMatch should return correct JSON"() {
        when:
        def matcher = performGetAndMatch("/api/test/hello")

        then:
        matcher.readString("message") == "hello"
    }

    def "performPostAndMatch should echo request body"() {
        given:
        def body = '{"name":"test"}'

        when:
        def matcher = performPostAndMatch("/api/test/echo", body)

        then:
        matcher.readString("name") == "test"
    }

    def "performPutAndMatch should update and return"() {
        given:
        def body = '{"name":"updated"}'

        when:
        def matcher = performPutAndMatch("/api/test/update", body)

        then:
        matcher.readString("name") == "updated"
        matcher.readBoolean("updated") == true
    }

    def "performDeleteAndMatch should return deleted flag"() {
        when:
        def matcher = performDeleteAndMatch("/api/test/remove")

        then:
        matcher.readBoolean("deleted") == true
    }
}
