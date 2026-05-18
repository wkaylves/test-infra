package com.github.kaylves.test.infra.storage.nosql;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedMongoDBContainer extends MongoDBContainer {

    private static final String DEFAULT_IMAGE = "mongo:7";
    private static volatile SharedMongoDBContainer INSTANCE;

    private SharedMongoDBContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
    }

    private SharedMongoDBContainer(String image) {
        super(DockerImageName.parse(image));
    }

    public static SharedMongoDBContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedMongoDBContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedMongoDBContainer();
                }
            }
        }
        return INSTANCE;
    }

    public static SharedMongoDBContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedMongoDBContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedMongoDBContainer(image);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("MONGODB_URI", getReplicaSetUrl());
    }

    @Override
    public void stop() {
    }
}
