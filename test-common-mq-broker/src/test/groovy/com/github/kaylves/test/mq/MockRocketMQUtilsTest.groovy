package com.github.kaylves.test.mq

import org.apache.rocketmq.client.producer.SendStatus
import org.apache.rocketmq.spring.core.RocketMQTemplate
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Specification

class MockRocketMQUtilsTest extends Specification {

    def "mockRocketMQTemplate should return non-null template"() {
        when:
        def template = MockRocketMQUtils.mockRocketMQTemplate()

        then:
        template != null
    }

    def "mockRocketMQTemplate should return mock of RocketMQTemplate type"() {
        when:
        def template = MockRocketMQUtils.mockRocketMQTemplate()

        then:
        template instanceof RocketMQTemplate
    }

    def "syncSend should return SendResult with SEND_OK status"() {
        given:
        def template = MockRocketMQUtils.mockRocketMQTemplate()
        def message = MessageBuilder.withPayload("test-payload").build()

        when:
        def result = template.syncSend("test-topic", message)

        then:
        result != null
        result.sendStatus == SendStatus.SEND_OK
    }

    def "syncSend should work with any topic"() {
        given:
        def template = MockRocketMQUtils.mockRocketMQTemplate()
        def message = MessageBuilder.withPayload("data").build()

        when:
        def result1 = template.syncSend("topic-a", message)
        def result2 = template.syncSend("topic-b", message)

        then:
        result1.sendStatus == SendStatus.SEND_OK
        result2.sendStatus == SendStatus.SEND_OK
    }

    def "syncSend should work with any message payload"() {
        given:
        def template = MockRocketMQUtils.mockRocketMQTemplate()
        def message1 = MessageBuilder.withPayload("string-payload").build()
        def message2 = MessageBuilder.withPayload('{"key":"value"}'.bytes).build()

        when:
        def result1 = template.syncSend("topic", message1)
        def result2 = template.syncSend("topic", message2)

        then:
        result1.sendStatus == SendStatus.SEND_OK
        result2.sendStatus == SendStatus.SEND_OK
    }
}
