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
package cn.odboy.app.framework.kubernetes.core.vo;

import cn.hutool.core.lang.Assert;
import lombok.Value;

/**
 * 通用参数 命名空间
 *
 * @author odboy
 * @date 2025-05-01
 */
@Value
public class ArgsNamespaceNameVo {
    String value;

    public ArgsNamespaceNameVo(String value) {
        Assert.notBlank(value, "命名空间名称不能为空");
        this.value = value;
    }
}
