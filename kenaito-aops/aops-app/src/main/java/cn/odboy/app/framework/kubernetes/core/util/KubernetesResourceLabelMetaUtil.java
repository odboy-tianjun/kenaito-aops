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
package cn.odboy.app.framework.kubernetes.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.app.framework.kubernetes.core.constant.KubernetesResourceLabelEnum;
import java.util.HashMap;
import java.util.Map;

/**
 * K8s标签
 *
 * @author odboy
 * @date 2021-03-17
 */
public class KubernetesResourceLabelMetaUtil {
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
