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
package cn.odboy.app.framework.minio.core.vo;

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
public class MinioStorageVo extends MyObject {
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
