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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class ContainerdSpecConfigUpdateArgs extends MyObject {
    @NotNull(message = "ID必填")
    private Long id;
    @NotBlank(message = "规格名称必填")
    private String specName;
    @NotNull(message = "CPU数量必填")
    @Min(message = "最小值为1", value = 1)
    @Max(message = "最大值为256", value = 256)
    private Integer cpuNum;
    @NotNull(message = "内存大小必填")
    @Min(message = "最小值为1", value = 1)
    @Max(message = "最大值为512", value = 512)
    private Integer memNum;
    @NotNull(message = "磁盘大小必填")
    @Min(message = "最小值为60", value = 60)
    @Max(message = "最大值为180", value = 180)
    private Integer diskSize;
}
