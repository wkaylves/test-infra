package com.github.kaylves.test.infra.storage.rdbms;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedPostgreSQLContainer extends PostgreSQLContainer<SharedPostgreSQLContainer> {

    private static final String DEFAULT_IMAGE = "postgres:15";
    private static volatile SharedPostgreSQLContainer INSTANCE;

    private SharedPostgreSQLContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
    }

    private SharedPostgreSQLContainer(String image) {
        super(DockerImageName.parse(image));
    }

    public static SharedPostgreSQLContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedPostgreSQLContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedPostgreSQLContainer()
                            .withDatabaseName("test_db")
                            .withUsername("test")
                            .withPassword("test");
                }
            }
        }
        return INSTANCE;
    }

    public static SharedPostgreSQLContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedPostgreSQLContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedPostgreSQLContainer(image)
                            .withDatabaseName("test_db")
                            .withUsername("test")
                            .withPassword("test");
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("POSTGRES_HOST", getHost());
        System.setProperty("POSTGRES_PORT", getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT).toString());
        System.setProperty("POSTGRES_DB", getDatabaseName());
        System.setProperty("POSTGRES_USER", getUsername());
        System.setProperty("POSTGRES_PASSWORD", getPassword());
    }

    @Override
    public void stop() {
    }
}
