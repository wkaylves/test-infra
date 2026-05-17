package com.github.kaylves.test.http

import feign.FeignException

class OpenFeignIntegrationSpec extends BaseWireMockSpec {

    private UserSpringFeignClient client

    def setup() {
        client = feignClient(UserSpringFeignClient)
    }

    // ==================== JSON ====================

    def "GET - 预设响应后调用"() {
        given:
        willReturn(UserSpringFeignClient, "getUser")
                .response(user(1, "Alice", "alice@example.com"))

        when:
        def result = client.getUser(1L)

        then:
        result["name"] == "Alice"
        result["email"] == "alice@example.com"
    }

    def "GET - 列表查询"() {
        given:
        willReturn(UserSpringFeignClient, "listUsers")
                .response([user(1, "Alice", null), user(2, "Bob", null)])

        when:
        def users = client.listUsers()

        then:
        users.size() == 2
        users[0]["name"] == "Alice"
        users[1]["name"] == "Bob"
    }

    def "GET - 404 错误"() {
        given:
        willReturn(UserSpringFeignClient, "getUser")
                .response(404, error("not found"))

        when:
        client.getUser(999L)

        then:
        thrown(FeignException)
    }

    def "GET - 500 错误"() {
        given:
        willReturn(UserSpringFeignClient, "getUser")
                .response(500, error("internal error"))

        when:
        client.getUser(500L)

        then:
        thrown(FeignException)
    }

    def "POST - 创建用户"() {
        given:
        willReturn(UserSpringFeignClient, "createUser")
                .response(user(3, "Charlie", "charlie@example.com"))

        when:
        def result = client.createUser(user(null, "Charlie", "charlie@example.com"))

        then:
        result["id"] == 3
        result["name"] == "Charlie"
    }

    def "POST - 400 错误"() {
        given:
        willReturn(UserSpringFeignClient, "createUser")
                .response(400, error("name is required"))

        when:
        client.createUser(map("email", "no-name@example.com"))

        then:
        thrown(FeignException)
    }

    def "PUT - 更新用户"() {
        given:
        willReturn(UserSpringFeignClient, "updateUser")
                .response(user(1, "Alice Updated", "alice@example.com"))

        when:
        def result = client.updateUser(1L, map("name", "Alice Updated"))

        then:
        result["name"] == "Alice Updated"
    }

    def "DELETE - 删除用户"() {
        given:
        willReturn(UserSpringFeignClient, "deleteUser")
                .response(message("deleted"))

        when:
        def result = client.deleteUser(1L)

        then:
        result["message"] == "deleted"
    }

    // ==================== Form ====================

    def "POST Form - 表单提交"() {
        given:
        willReturn(UserSpringFeignClient, "createUserForm")
                .response(user(4, "Dave", "dave@example.com"))

        when:
        def result = client.createUserForm([name: "Dave", email: "dave@example.com"])

        then:
        result["id"] == 4
        result["name"] == "Dave"
    }

    // ==================== XML ====================

    def "POST XML - XML 请求体"() {
        given:
        def xmlRequest = "<user><name>Eve</name><email>eve@example.com</email></user>"
        def xmlResponse = "<user><id>5</id><name>Eve</name><email>eve@example.com</email></user>"
        willReturn(UserSpringFeignClient, "createUserXml").response(xmlResponse)

        when:
        def result = client.createUserXml(xmlRequest)

        then:
        result.contains("<id>5</id>")
        result.contains("<name>Eve</name>")
    }

    def "GET XML - XML 响应"() {
        given:
        def xmlResponse = "<user><id>1</id><name>Alice</name><email>alice@example.com</email></user>"
        willReturn(UserSpringFeignClient, "getUserXml").response(xmlResponse)

        when:
        def result = client.getUserXml(1L)

        then:
        result.contains("<name>Alice</name>")
        result.contains("<email>alice@example.com</email>")
    }

    // ==================== Text ====================

    def "POST Text - 纯文本请求体"() {
        given:
        willReturn(UserSpringFeignClient, "createUserText")
                .response("User created: Frank")

        when:
        def result = client.createUserText("Frank|frank@example.com")

        then:
        result == "User created: Frank"
    }

    def "GET Text - 纯文本响应"() {
        given:
        willReturn(UserSpringFeignClient, "getUserText")
                .response("Alice|alice@example.com")

        when:
        def result = client.getUserText(1L)

        then:
        result == "Alice|alice@example.com"
    }
}