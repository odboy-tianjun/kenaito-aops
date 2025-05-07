/*
 *  Copyright 2021-2025 Tian Jun
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
package cn.odboy.framework.kubernetes.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.framework.kubernetes.constant.KubernetesResourceLabelEnum;
import java.util.HashMap;
import java.util.Map;

/**
 * K8s标签匹配
 *
 * @author odboy
 * @date 2021-03-17
 */
public class KubernetesResourceLabelSelectorUtil {
    /**
     * 监听中标签匹配
     */
    public static boolean match(Map<String, String> selector, Map<String, String> labels) {
        // 忽略没有打标签的资源
        if (CollUtil.isEmpty(selector)) {
            return false;
        }
        // labels为空则返回所有
        if (labels == null || labels.keySet().isEmpty()) {
            return true;
        }
        for (String key : labels.keySet()) {
            if (selector.containsKey(key)) {
                return selector.get(key).equals(labels.get(key));
            }
        }
        return false;
    }

    public static String genLabelSelectorExpression(Map<String, String> labelSelector) {
        String labelSelectorStr = null;
        if (CollUtil.isNotEmpty(labelSelector)) {
            // labelSelectorStr = "key1=value1,key2=value2";
            StringBuilder tempBuilder = new StringBuilder();
            for (Map.Entry<String, String> kvEntry : labelSelector.entrySet()) {
                tempBuilder.append(kvEntry.getKey()).append("=").append(kvEntry.getValue()).append(",");
            }
            if (!tempBuilder.isEmpty()) {
                tempBuilder.deleteCharAt(tempBuilder.length() - 1);
            }
            if (StrUtil.isNotBlank(tempBuilder)) {
                labelSelectorStr = tempBuilder.toString();
            }
        }
        return labelSelectorStr;
    }

    public static Map<String, String> getLabelsByAppName(String appName) {
        return new HashMap<>(1) {{
            put(KubernetesResourceLabelEnum.AppName.getCode(), appName);
        }};
    }

    public static String getLabelsStrByAppName(String appName) {
        return KubernetesResourceLabelEnum.AppName.getCode() + "=" + appName;
    }
}
