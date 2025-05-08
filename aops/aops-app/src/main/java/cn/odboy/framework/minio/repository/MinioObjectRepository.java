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
package cn.odboy.framework.minio.repository;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.odboy.exception.BadRequestException;
import cn.odboy.framework.errorhandler.CustomApiExceptionCatch;
import cn.odboy.framework.minio.config.MinioProperties;
import cn.odboy.framework.minio.constant.MinioConst;
import cn.odboy.framework.minio.context.MinioApiClientManager;
import cn.odboy.framework.minio.model.MinioStorageResponse;
import cn.odboy.redis.RedisHelper;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Minio Bucket's Object
 *
 * @author odboy
 * @date 2025-01-16
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioObjectRepository {
    private static final String CACHE_KEY = "minio:file:previewUrl:";
    private final MinioApiClientManager minioClientAdmin;
    private final MinioProperties minioProperties;
    private final RedisHelper redisHelper;

    /**
     * @param region     地域编码
     * @param bucketName bucket名称
     * @return /
     */
    @SneakyThrows
    @CustomApiExceptionCatch(description = "根据region和bucketName查询对象", throwException = false)
    public List<MinioStorageResponse> describeObjectListByRegionWithBucketName(String region, String bucketName) {
        List<MinioStorageResponse> result = new ArrayList<>();
        try (MinioClient client = minioClientAdmin.getClient()) {
            Iterable<Result<Item>> iterables = client.listObjects(ListObjectsArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .build());
            for (Result<Item> iterable : iterables) {
                Item item = iterable.get();
                MinioStorageResponse record = new MinioStorageResponse();
                record.setFileName(item.objectName());
                String suffix = FileUtil.getSuffix(item.objectName());
                record.setSuffix(suffix);
                record.setFileType(cn.odboy.util.FileUtil.getFileType(suffix));
                record.setFileSize(item.size());
                record.setObjectName(item.objectName());
                record.setETag(item.etag());
                record.setVersionId(item.versionId());
                record.setBucketName(bucketName);
                record.setRegion(region);
                result.add(record);
            }
            return result;
        } catch (Exception e) {
            log.error("获取所有对象失败");
            return new ArrayList<>();
        }
    }

    /**
     * @param bucketName bucket名称
     * @return /
     */
    @SneakyThrows
    @CustomApiExceptionCatch(description = "根据bucketName查询默认region中的对象", throwException = false)
    public List<MinioStorageResponse> describeDefaultRegionObjectListByBucketName(String bucketName) {
        return describeObjectListByRegionWithBucketName(MinioConst.DEFAULT_REGION, bucketName);
    }

    /**
     * 获取region中bucketName内的对象
     *
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     * @return /
     */
    @SneakyThrows
    @CustomApiExceptionCatch(description = "根据region、bucketName和object相对路径查询对象", throwException = false)
    public GetObjectResponse describeObject(String region, String bucketName, String object) {
        try (MinioClient client = minioClientAdmin.getClient()) {
            return client.getObject(GetObjectArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("获取对象失败", e);
            throw new BadRequestException("获取对象失败");
        }
    }

    /**
     * 获取默认region中bucketName内的对象
     *
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     * @return /
     */
    @SneakyThrows
    @CustomApiExceptionCatch(description = "根据bucketName和object相对路径查询默认region中的对象", throwException = false)
    public GetObjectResponse describeDefaultRegionObject(String bucketName, String object) {
        try (MinioClient client = minioClientAdmin.getClient()) {
            return client.getObject(GetObjectArgs.builder()
                    .region(MinioConst.DEFAULT_REGION)
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("获取对象失败", e);
            throw new BadRequestException("获取对象失败");
        }
    }

    /**
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    @CustomApiExceptionCatch(description = "生成对象预览地址")
    public String generateFilePreviewUrl(String region, String bucketName, String object) throws Exception {
        try (MinioClient client = minioClientAdmin.getClient()) {
            String redisKey = CACHE_KEY + Base64Encoder.encode(object, StandardCharsets.UTF_8);
            String filePreviewUrl = redisHelper.get(redisKey, String.class);
            if (filePreviewUrl != null) {
                return filePreviewUrl;
            }
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .expiry(minioProperties.getShareExpireTime(), TimeUnit.DAYS)
                    .region(region)
                    .bucket(bucketName)
                    .object(object)
                    .build();
            String presignedObjectUrl = client.getPresignedObjectUrl(getPresignedObjectUrlArgs);
            redisHelper.set(redisKey, presignedObjectUrl, Math.round(minioProperties.getShareExpireTime() * 24 * 0.8), TimeUnit.HOURS);
            return presignedObjectUrl;
        } catch (Exception e) {
            log.error("获取对象分享链接失败", e);
            throw new BadRequestException("获取对象分享链接失败");
        }
    }

    /**
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    @CustomApiExceptionCatch(description = "生成默认region中对象预览地址")
    public String generateDefaultRegionFilePreviewUrl(String bucketName, String object) throws Exception {
        return generateFilePreviewUrl(MinioConst.DEFAULT_REGION, bucketName, object);
    }

    /**
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    @CustomApiExceptionCatch(description = "删除region下的bucketName中的对象")
    public void deleteObject(String region, String bucketName, String object) throws Exception {
        try (MinioClient client = minioClientAdmin.getClient()) {
            client.removeObject(RemoveObjectArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("删除对象失败", e);
            throw new BadRequestException("删除对象失败");
        }
    }

    /**
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    @CustomApiExceptionCatch(description = "删除默认region下的bucketName中的对象")
    public void deleteDefaultRegionObject(String bucketName, String object) throws Exception {
        try (MinioClient client = minioClientAdmin.getClient()) {
            client.removeObject(RemoveObjectArgs.builder()
                    .region(MinioConst.DEFAULT_REGION)
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("删除对象失败", e);
            throw new BadRequestException("删除对象失败");
        }
    }

    /**
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     * @param file       上传的文件
     */
    @CustomApiExceptionCatch(description = "上传对象到region中bucketName内")
    public ObjectWriteResponse uploadObjectToBucket(String region, String bucketName, String object, MultipartFile file) throws Exception {
        try (MinioClient client = minioClientAdmin.getClient()) {
            PutObjectArgs uploadObjectArgs = PutObjectArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .object(object)
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .contentType(FileTypeUtil.getType(file.getInputStream()))
                    .build();
            ObjectWriteResponse response = client.putObject(uploadObjectArgs);
            log.info("上传对象到region={}，bucket={}成功", region, bucketName);
            return response;
        } catch (Exception e) {
            String message = "上传对象到region={}，bucket={}失败";
            log.error(message, region, bucketName, e);
            try {
                deleteObject(region, bucketName, object);
            } catch (Exception e2) {
                log.error(e2.getMessage(), e2);
            }
            throw new BadRequestException(message);
        }
    }

    /**
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     * @param file       上传的文件
     */
    @CustomApiExceptionCatch(description = "上传对象到默认region中bucketName内")
    public ObjectWriteResponse uploadObjectToDefaultRegionBucket(String bucketName, String object, MultipartFile file) throws Exception {
        return uploadObjectToBucket(MinioConst.DEFAULT_REGION, bucketName, object, file);
    }

    /**
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    @CustomApiExceptionCatch(description = "获取region中bucketName内的对象数据流分片")
    public InputStream descripeObjectOffsetInputStream(String region, String bucketName, String object, long offset, long length) throws Exception {
        try (MinioClient client = minioClientAdmin.getClient()) {
            return client.getObject(
                    GetObjectArgs.builder()
                            .region(region)
                            .bucket(bucketName)
                            .object(object)
                            .offset(offset)
                            .length(length)
                            .build());
        } catch (Exception e) {
            log.error("获取对象InputStream失败", e);
            throw new BadRequestException("获取对象InputStream失败");
        }
    }

    /**
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    @CustomApiExceptionCatch(description = "获取默认region中bucketName内的对象数据流分片")
    public InputStream descripeDefaultRegionObjectOffsetInputStream(String bucketName, String object, long offset, long length) throws Exception {
        return descripeObjectOffsetInputStream(MinioConst.DEFAULT_REGION, bucketName, object, offset, length);
    }

    /**
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    @CustomApiExceptionCatch(description = "获取region中bucketName内的对象数据流")
    public InputStream descripeObjectInputStream(String region, String bucketName, String object) throws Exception {
        try (MinioClient client = minioClientAdmin.getClient()) {
            return client.getObject(GetObjectArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("获取对象InputStream失败", e);
            throw new BadRequestException("获取对象InputStream失败");
        }
    }

    /**
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    @CustomApiExceptionCatch(description = "获取默认region中bucketName内的对象数据流")
    public InputStream descripeDefaultRegionObjectInputStream(String bucketName, String object) throws Exception {
        return descripeObjectInputStream(MinioConst.DEFAULT_REGION, bucketName, object);
    }
}
