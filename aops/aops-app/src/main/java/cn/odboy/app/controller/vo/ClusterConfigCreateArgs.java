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
package cn.odboy.app.controller.vo;

import cn.odboy.common.pojo.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;


@Data
@EqualsAndHashCode(callSuper = false)
public class ClusterConfigCreateArgs extends MyObject {
    @NotBlank(message = "环境编码必填")
    private String envCode;
    @NotBlank(message = "集群编码必填")
    private String clusterCode;
    @NotBlank(message = "集群名称必填")
    private String clusterName;
    @NotBlank(message = "集群配置必填")
    private String clusterConfigContent;
    @NotBlank(message = "集群默认应用镜像必填")
    private String clusterDefaultAppImage;
}
