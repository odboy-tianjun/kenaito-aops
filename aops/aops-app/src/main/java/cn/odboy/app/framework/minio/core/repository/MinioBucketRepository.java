/*
 *  Copyright 2021-2025 Odboy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.app.framework.minio.core.repository;

import cn.odboy.core.framework.errorhandler.annotaions.CustomApiExceptionCatch;
import cn.odboy.app.framework.minio.core.constant.MinioConst;
import cn.odboy.app.framework.minio.core.context.MinioApiClientManager;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.messages.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Minio Bucket
 *
 * @author odboy
 * @date 2025-01-16
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioBucketRepository {
    private final MinioApiClientManager minioApiClientManager;

    /**
     * @param region     地域编码
     * @param bucketName bucket名称
     */
    @CustomApiExceptionCatch(description = "根据bucketName创建Bucket")
    public void createBucket(String region, String bucketName) throws Exception {
        if (exitsBucketByBucketName(region, bucketName)) {
            log.info("region={}，bucket={}已存在，不会执行任何操作", region, bucketName);
            return;
        }
        try (MinioClient client = minioApiClientManager.getClient()) {
            client.makeBucket(MakeBucketArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .objectLock(false)
                    .build());
        }
    }

    /**
     * @param region     地域编码
     * @param bucketName bucket名称
     */
    @SneakyThrows
    @CustomApiExceptionCatch(description = "根据bucketName判断Bucket是否存在", throwException = false)
    public boolean exitsBucketByBucketName(String region, String bucketName) {
        try (MinioClient client = minioApiClientManager.getClient()) {
            return client.bucketExists(BucketExistsArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .build());
        }
    }

    /**
     * 判断default地域中是否存在
     *
     * @param bucketName bucket名称
     */
    public boolean exitsBucket(String bucketName) {
        return exitsBucketByBucketName(MinioConst.DEFAULT_REGION, bucketName);
    }

    @SneakyThrows
    @CustomApiExceptionCatch(description = "获取所有的bucket", throwException = false)
    public List<Bucket> describeBucketList() {
        try (MinioClient client = minioApiClientManager.getClient()) {
            return client.listBuckets();
        }
    }
}
