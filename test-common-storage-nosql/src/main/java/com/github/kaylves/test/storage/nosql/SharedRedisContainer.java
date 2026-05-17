package com.github.kaylves.test.storage.nosql;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedRedisContainer extends GenericContainer<SharedRedisContainer> {

    private static final String DEFAULT_IMAGE = "redis:7";
    private static final int REDIS_PORT = 6379;
    private static volatile SharedRedisContainer INSTANCE;

    private SharedRedisContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
        withExposedPorts(REDIS_PORT);
    }

    private SharedRedisContainer(String image) {
        super(DockerImageName.parse(image));
        withExposedPorts(REDIS_PORT);
    }

    public static SharedRedisContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedRedisContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedRedisContainer();
                }
            }
        }
        return INSTANCE;
    }

    public static SharedRedisContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedRedisContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedRedisContainer(image);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("REDIS_HOST", getHost());
        System.setProperty("REDIS_PORT", getMappedPort(REDIS_PORT).toString());
        System.setProperty("REDIS_PASSWORD", "");
    }

    @Override
    public void stop() {
    }

    public String getRedisUrl() {
        return "redis://" + getHost() + ":" + getMappedPort(REDIS_PORT);
    }
}
