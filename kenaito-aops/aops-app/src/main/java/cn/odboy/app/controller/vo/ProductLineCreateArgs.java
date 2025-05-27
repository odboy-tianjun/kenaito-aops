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
import javax.validation.constraints.Size;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = false)
public class ProductLineCreateArgs extends MyObject {
    @NotBlank(message = "产品名称必填")
    private String lineName;
    @NotNull(message = "产品负责人必填")
    @Size(min = 1, message = "产品负责人至少一人")
    private List<Long> owner;
    @NotNull(message = "运维负责人必填")
    @Size(min = 1, message = "运维负责人至少一人")
    private List<Long> pe;
    @NotBlank(message = "描述必填")
    private String remark;
}
