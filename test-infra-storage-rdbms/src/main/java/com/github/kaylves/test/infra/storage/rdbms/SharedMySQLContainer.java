package com.github.kaylves.test.infra.storage.rdbms;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedMySQLContainer extends MySQLContainer<SharedMySQLContainer> {

    private static final String DEFAULT_IMAGE = "mysql:8.0";
    private static volatile SharedMySQLContainer INSTANCE;

    private SharedMySQLContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
    }

    private SharedMySQLContainer(String image) {
        super(DockerImageName.parse(image));
    }

    public static SharedMySQLContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedMySQLContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedMySQLContainer()
                            .withDatabaseName("test_db")
                            .withUsername("test")
                            .withPassword("test");
                }
            }
        }
        return INSTANCE;
    }

    public static SharedMySQLContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedMySQLContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedMySQLContainer(image)
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
        System.setProperty("MYSQL_HOST", getHost());
        System.setProperty("MYSQL_PORT", getMappedPort(MySQLContainer.MYSQL_PORT).toString());
        System.setProperty("MYSQL_DATABASE", getDatabaseName());
        System.setProperty("MYSQL_USER", getUsername());
        System.setProperty("MYSQL_PASSWORD", getPassword());
    }

    @Override
    public void stop() {
    }
}
