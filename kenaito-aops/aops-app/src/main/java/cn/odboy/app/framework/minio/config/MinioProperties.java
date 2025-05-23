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
package cn.odboy.app.framework.minio.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Minio 配置
 *
 * @author odboy
 * @date 2025-01-16
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    /**
     * 客户端连接到 MinIO 服务器的 URL
     */
    private String endpoint;
    /**
     * accessKey
     */
    private String accessKey;
    /**
     * secretKey
     */
    private String secretKey;
    /**
     * 预览链接过期时间
     */
    private Integer shareExpireTime;
}
