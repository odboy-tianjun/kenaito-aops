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
package cn.odboy.app.controller.cmdb.vo;

import cn.odboy.common.pojo.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;


@Data
@EqualsAndHashCode(callSuper = false)
public class NetworkIngressCreateArgs extends MyObject {
    @NotBlank(message = "环境编码必填")
    private String envCode;
    @NotBlank(message = "应用名称必填")
    private String appName;
    @NotBlank(message = "绑定域名必填")
    private String hostname;
    @NotBlank(message = "网络类型必填")
    private String networkType;
    @NotBlank(message = "绑定的ServiceName必填")
    private String serviceName;
    @NotBlank(message = "绑定的路径必填")
    private String servicePath;
    private String remark;
}
