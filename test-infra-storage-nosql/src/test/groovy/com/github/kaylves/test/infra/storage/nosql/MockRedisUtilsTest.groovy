package com.github.kaylves.test.infra.storage.nosql

import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import spock.lang.Specification

class MockRedisUtilsTest extends Specification {

    def "mockRedisTemplate should return non-null RedisTemplate"() {
        when:
        def template = MockRedisUtils.mockRedisTemplate()

        then:
        template != null
    }

    def "mockRedisTemplate should return mock of RedisTemplate type"() {
        when:
        def template = MockRedisUtils.mockRedisTemplate()

        then:
        template instanceof RedisTemplate
    }

    // ==================== opsForValue ====================

    def "opsForValue should return non-null ValueOperations"() {
        given:
        def template = MockRedisUtils.mockRedisTemplate()

        when:
        def valueOps = template.opsForValue()

        then:
        valueOps != null
        valueOps instanceof ValueOperations
    }

    def "opsForValue.get should return null by default"() {
        given:
        def template = MockRedisUtils.mockRedisTemplate()

        when:
        def result = template.opsForValue().get("any-key")

        then:
        result == null
    }

    def "opsForValue.set should not throw exception"() {
        given:
        def template = MockRedisUtils.mockRedisTemplate()

        when:
        template.opsForValue().set("key", "value")

        then:
        noExceptionThrown()
    }

    def "opsForValue.set with expiry should not throw exception"() {
        given:
        def template = MockRedisUtils.mockRedisTemplate()

        when:
        template.opsForValue().set("key", "value", 60, java.util.concurrent.TimeUnit.SECONDS)

        then:
        noExceptionThrown()
    }

    // ==================== opsForHash ====================

    def "opsForHash should return non-null HashOperations"() {
        given:
        def template = MockRedisUtils.mockRedisTemplate()

        when:
        def hashOps = template.opsForHash()

        then:
        hashOps != null
        hashOps instanceof HashOperations
    }

    // ==================== delete ====================

    def "delete should return true by default"() {
        given:
        def template = MockRedisUtils.mockRedisTemplate()

        when:
        def result = template.delete("any-key")

        then:
        result == true
    }

    // ==================== hasKey ====================

    def "hasKey should return false by default"() {
        given:
        def template = MockRedisUtils.mockRedisTemplate()

        when:
        def result = template.hasKey("any-key")

        then:
        result == false
    }

    // ==================== expire ====================

    def "expire should return true by default"() {
        given:
        def template = MockRedisUtils.mockRedisTemplate()

        when:
        def result = template.expire("any-key", 60, java.util.concurrent.TimeUnit.SECONDS)

        then:
        result == true
    }

    // ==================== 可覆盖默认行为 ====================

    def "default mock behavior should be overridable"() {
        given:
        def template = MockRedisUtils.mockRedisTemplate()
        org.mockito.Mockito.when(template.opsForValue().get("special-key")).thenReturn("special-value")

        when:
        def result = template.opsForValue().get("special-key")

        then:
        result == "special-value"
    }

    def "hasKey default should be overridable to true"() {
        given:
        def template = MockRedisUtils.mockRedisTemplate()
        org.mockito.Mockito.when(template.hasKey("existing-key")).thenReturn(true)

        when:
        def result = template.hasKey("existing-key")

        then:
        result == true
    }
}
