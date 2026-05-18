package com.github.kaylves.test.infra.mq;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedPulsarContainer extends GenericContainer<SharedPulsarContainer> {

    private static final String DEFAULT_IMAGE = "apachepulsar/pulsar:3.1.0";
    private static final int BROKER_PORT = 6650;
    private static final int WEB_PORT = 8080;
    private static volatile SharedPulsarContainer INSTANCE;

    private SharedPulsarContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
        withExposedPorts(BROKER_PORT, WEB_PORT);
        withCommand("bin/pulsar", "standalone");
    }

    private SharedPulsarContainer(String image) {
        super(DockerImageName.parse(image));
        withExposedPorts(BROKER_PORT, WEB_PORT);
        withCommand("bin/pulsar", "standalone");
    }

    public static SharedPulsarContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedPulsarContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedPulsarContainer();
                }
            }
        }
        return INSTANCE;
    }

    public static SharedPulsarContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedPulsarContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedPulsarContainer(image);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("PULSAR_BROKER_URL", "pulsar://" + getHost() + ":" + getMappedPort(BROKER_PORT));
        System.setProperty("PULSAR_WEB_URL", "http://" + getHost() + ":" + getMappedPort(WEB_PORT));
    }

    @Override
    public void stop() {
    }
}
