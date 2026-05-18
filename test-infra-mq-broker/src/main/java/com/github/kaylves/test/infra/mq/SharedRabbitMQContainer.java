package com.github.kaylves.test.infra.mq;

import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedRabbitMQContainer extends RabbitMQContainer {

    private static final String DEFAULT_IMAGE = "rabbitmq:3.12-management";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";
    private static volatile SharedRabbitMQContainer INSTANCE;

    private SharedRabbitMQContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
        withUser(USERNAME, PASSWORD);
    }

    private SharedRabbitMQContainer(String image) {
        super(DockerImageName.parse(image));
        withUser(USERNAME, PASSWORD);
    }

    public static SharedRabbitMQContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedRabbitMQContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedRabbitMQContainer();
                }
            }
        }
        return INSTANCE;
    }

    public static SharedRabbitMQContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedRabbitMQContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedRabbitMQContainer(image);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("RABBITMQ_HOST", getHost());
        System.setProperty("RABBITMQ_PORT", String.valueOf(getAmqpPort()));
        System.setProperty("RABBITMQ_USERNAME", USERNAME);
        System.setProperty("RABBITMQ_PASSWORD", PASSWORD);
    }

    @Override
    public void stop() {
    }
}
