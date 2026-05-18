package com.github.kaylves.test.infra.storage.search;

import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedElasticsearchContainer extends ElasticsearchContainer {

    private static final String DEFAULT_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:8.10.0";
    private static final int HTTP_PORT = 9200;
    private static volatile SharedElasticsearchContainer INSTANCE;

    private SharedElasticsearchContainer() {
        super(DockerImageName.parse(DEFAULT_IMAGE));
        withEnv("xpack.security.enabled", "false");
    }

    private SharedElasticsearchContainer(String image) {
        super(DockerImageName.parse(image));
        withEnv("xpack.security.enabled", "false");
    }

    public static SharedElasticsearchContainer getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedElasticsearchContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedElasticsearchContainer();
                }
            }
        }
        return INSTANCE;
    }

    public static SharedElasticsearchContainer getInstance(String image) {
        if (INSTANCE == null) {
            synchronized (SharedElasticsearchContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharedElasticsearchContainer(image);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("ELASTICSEARCH_HOST", getHost());
        System.setProperty("ELASTICSEARCH_HTTP_PORT", getMappedPort(HTTP_PORT).toString());
    }

    @Override
    public void stop() {
    }
}
