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

import cn.odboy.common.model.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 仅修改集群默认应用负载配置
 *
 * @author odboy
 * @date 2025-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ClusterConfigModifyDefaultAppYmlArgs extends MyObject {
    @NotNull(message = "ID必填")
    private Long id;
    @NotBlank(message = "应用负载Yml必填")
    private String clusterDefaultAppYaml;
}
