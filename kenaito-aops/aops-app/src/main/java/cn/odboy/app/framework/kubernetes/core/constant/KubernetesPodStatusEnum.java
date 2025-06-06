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
package cn.odboy.app.framework.kubernetes.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Kubernetes Pod状态
 *
 * @author odboy
 * @date 2025-01-12
 */
@Getter
@AllArgsConstructor
public enum KubernetesPodStatusEnum {
    /**
     * Pod已经创建，但尚未被调度到某个节点上，或者正在下载镜像、加载依赖项等。可能的原因包括节点资源不足、调度问题等
     */
    Pending("Pending", "等待中"),
    /**
     * Pod已经被调度到某个节点上，并且所有容器都已经被创建并运行。只要有一个容器在运行，Pod就处于Running状态
     */
    Running("Running", "运行中"),
    /**
     * Pod中的所有容器都成功完成并终止，且不会被重启。通常用于一次性任务，如Job
     */
    Succeeded("Succeeded", "正常终止"),
    /**
     * Pod中的所有容器都已终止，但至少有一个容器因为失败而终止。容器以非0状态退出或被系统终止
     */
    Failed("Failed", "异常终止"),
    /**
     * 由于某些原因无法获取Pod的状态信息，通常是与Pod通信出现问题
     */
    Unknown("Unknown", "未知状态");
    private final String code;
    private final String desc;

    public static KubernetesPodStatusEnum getByCode(String code) {
        for (KubernetesPodStatusEnum item : KubernetesPodStatusEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
