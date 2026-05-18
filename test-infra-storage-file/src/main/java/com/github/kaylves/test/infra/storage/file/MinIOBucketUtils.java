package com.github.kaylves.test.infra.storage.file;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.BucketExistsArgs;

public class MinIOBucketUtils {

    private MinIOBucketUtils() {
    }

    public static void createBucket(String endpoint, String accessKey, String secretKey, String bucketName) {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            if (!client.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName).build())) {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bucket: " + bucketName, e);
        }
    }
}
