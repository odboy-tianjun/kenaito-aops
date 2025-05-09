package cn.odboy.app.framework.minio.model;

import cn.odboy.common.pojo.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Minio存储 对象信息
 *
 * @author odboy
 * @date 2025-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MinioStorageResponse extends MyObject {
    private String fileName;
    private String suffix;
    private String fileType;
    private long fileSize;
    private String objectName;
    private String eTag;
    private String versionId;
    private String bucketName;
    private String region;
}
