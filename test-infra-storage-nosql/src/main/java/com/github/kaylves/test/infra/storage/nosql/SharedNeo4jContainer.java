package com.github.kaylves.test.infra.storage.nosql;

import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedNeo4jContainer extends Neo4jContainer<SharedNeo4jContainer> {

    private static final String DEFAULT_IMAGE = "neo4j:5";
    private static volatile SharedNeo4jContainer INSTANCE;

    private SharedNeo4jContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
        withoutAuthentication();
    }

    private SharedNeo4jContainer(String image) {
        super(DockerImageName.parse(image));
        withoutAuthentication();
    }

    public static SharedNeo4jContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedNeo4jContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedNeo4jContainer();
                }
            }
        }
        return INSTANCE;
    }

    public static SharedNeo4jContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedNeo4jContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedNeo4jContainer(image);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("NEO4J_URI", getBoltUrl());
    }

    @Override
    public void stop() {
    }
}
