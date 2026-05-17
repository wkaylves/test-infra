package com.github.kaylves.test.mq;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedRocketMQContainer extends GenericContainer<SharedRocketMQContainer> {

    private static final String ROCKETMQ_VERSION = "5.1.4";
    private static final String DEFAULT_IMAGE = "apache/rocketmq:" + ROCKETMQ_VERSION;
    private static final int NAMESRV_PORT = 9876;
    private static volatile SharedRocketMQContainer INSTANCE;

    private final String imageName;
    private GenericContainer<?> brokerContainer;

    private SharedRocketMQContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
        this.imageName = DEFAULT_IMAGE;
        withExposedPorts(NAMESRV_PORT);
        withCommand("sh", "mqnamesrv");
    }

    private SharedRocketMQContainer(String image) {
        super(DockerImageName.parse(image));
        this.imageName = image;
        withExposedPorts(NAMESRV_PORT);
        withCommand("sh", "mqnamesrv");
    }

    public static SharedRocketMQContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedRocketMQContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedRocketMQContainer();
                }
            }
        }
        return INSTANCE;
    }

    public static SharedRocketMQContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedRocketMQContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedRocketMQContainer(image);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();
        String namesrvAddr = getHost() + ":" + getMappedPort(NAMESRV_PORT);
        System.setProperty("ROCKETMQ_NAMESRV_ADDR", namesrvAddr);
        startBroker(namesrvAddr);
    }

    private void startBroker(String namesrvAddr) {
        brokerContainer = new GenericContainer<>(DockerImageName.parse(imageName))
                .withExposedPorts(10911)
                .withCommand("sh", "mqbroker", "-n", namesrvAddr)
                .withEnv("JAVA_OPT_EXT", "-server -Xms256m -Xmx256m");
        brokerContainer.start();
        System.setProperty("ROCKETMQ_BROKER_ADDR",
                brokerContainer.getHost() + ":" + brokerContainer.getMappedPort(10911));
    }

    @Override
    public void stop() {
    }

    public String getNameSrvAddr() {
        return getHost() + ":" + getMappedPort(NAMESRV_PORT);
    }

    public String getBrokerAddr() {
        if (brokerContainer != null) {
            return brokerContainer.getHost() + ":" + brokerContainer.getMappedPort(10911);
        }
        return null;
    }
}
