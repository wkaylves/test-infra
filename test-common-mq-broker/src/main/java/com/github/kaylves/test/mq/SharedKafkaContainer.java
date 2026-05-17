package com.github.kaylves.test.mq;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedKafkaContainer extends KafkaContainer {

    private static final String DEFAULT_IMAGE = "confluentinc/cp-kafka:7.5.0";
    private static volatile SharedKafkaContainer INSTANCE;

    private SharedKafkaContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
    }

    private SharedKafkaContainer(String image) {
        super(DockerImageName.parse(image));
    }

    public static SharedKafkaContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedKafkaContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedKafkaContainer();
                }
            }
        }
        return INSTANCE;
    }

    public static SharedKafkaContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedKafkaContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedKafkaContainer(image);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", getBootstrapServers());
    }

    @Override
    public void stop() {
    }
}
