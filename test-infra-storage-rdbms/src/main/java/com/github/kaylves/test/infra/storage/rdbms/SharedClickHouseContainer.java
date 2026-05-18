package com.github.kaylves.test.infra.storage.rdbms;

import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedClickHouseContainer extends ClickHouseContainer {

    private static final String DEFAULT_IMAGE = "clickhouse/clickhouse-server:23.8";
    private static final int HTTP_PORT = 8123;
    private static volatile SharedClickHouseContainer INSTANCE;

    private SharedClickHouseContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
    }

    private SharedClickHouseContainer(String image) {
        super(DockerImageName.parse(image));
    }

    public static SharedClickHouseContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedClickHouseContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedClickHouseContainer();
                }
            }
        }
        return INSTANCE;
    }

    public static SharedClickHouseContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedClickHouseContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedClickHouseContainer(image);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("CLICKHOUSE_HOST", getHost());
        System.setProperty("CLICKHOUSE_PORT", String.valueOf(getMappedPort(HTTP_PORT)));
    }

    @Override
    public void stop() {
    }
}
