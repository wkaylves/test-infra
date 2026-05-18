package com.github.kaylves.test.infra.mq;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class MockRocketMQUtils {

    private MockRocketMQUtils() {
    }

    public static RocketMQTemplate mockRocketMQTemplate() {
        RocketMQTemplate template = Mockito.mock(RocketMQTemplate.class);
        SendResult sendResult = new SendResult();
        sendResult.setSendStatus(SendStatus.SEND_OK);
        Mockito.when(template.syncSend(Mockito.anyString(), Mockito.any(Message.class))).thenReturn(sendResult);
        return template;
    }
}
