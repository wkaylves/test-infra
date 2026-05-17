package com.github.kaylves.test.storage.file;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedMinIOContainer extends GenericContainer<SharedMinIOContainer> {

    private static final String DEFAULT_IMAGE = "minio/minio:latest";
    private static final String ACCESS_KEY = "minioadmin";
    private static final String SECRET_KEY = "minioadmin";
    private static final int MINIO_PORT = 9000;
    private static final int CONSOLE_PORT = 9001;
    private static volatile SharedMinIOContainer INSTANCE;

    private SharedMinIOContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
        withCommand("server", "/data", "--console-address", ":9001");
        withExposedPorts(MINIO_PORT, CONSOLE_PORT);
        withEnv("MINIO_ROOT_USER", ACCESS_KEY);
        withEnv("MINIO_ROOT_PASSWORD", SECRET_KEY);
    }

    private SharedMinIOContainer(String image) {
        super(DockerImageName.parse(image));
        withCommand("server", "/data", "--console-address", ":9001");
        withExposedPorts(MINIO_PORT, CONSOLE_PORT);
        withEnv("MINIO_ROOT_USER", ACCESS_KEY);
        withEnv("MINIO_ROOT_PASSWORD", SECRET_KEY);
    }

    public static SharedMinIOContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedMinIOContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedMinIOContainer();
                }
            }
        }
        return INSTANCE;
    }

    public static SharedMinIOContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedMinIOContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedMinIOContainer(image);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("MINIO_ENDPOINT", "http://" + getHost() + ":" + getMappedPort(MINIO_PORT));
        System.setProperty("MINIO_ACCESS_KEY", ACCESS_KEY);
        System.setProperty("MINIO_SECRET_KEY", SECRET_KEY);
    }

    @Override
    public void stop() {
    }

    public String getEndpoint() {
        return "http://" + getHost() + ":" + getMappedPort(MINIO_PORT);
    }

    public String getAccessKey() {
        return ACCESS_KEY;
    }

    public String getSecretKey() {
        return SECRET_KEY;
    }

    public void createBucket(String bucketName) {
        MinIOBucketUtils.createBucket(getEndpoint(), getAccessKey(), getSecretKey(), bucketName);
    }
}
