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
package cn.odboy.app.framework.kubernetes.core.context;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.odboy.app.framework.kubernetes.config.KubernetesProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Kubernetes yaml模版管理
 *
 * @author odboy
 * @date 2025-05-08
 */
@Component
@RequiredArgsConstructor
public class KubernetesYamlTemplateManager {
    private final KubernetesProperties kubernetesProperties;

    private TemplateEngine getTemplateEngine() {
        return TemplateUtil.createEngine(new TemplateConfig("kubernetes/templates/" + kubernetesProperties.getTemplateVersion(), TemplateConfig.ResourceMode.CLASSPATH));
    }

    public String renderNamespaceYamlContent(Map<String, Object> params) {
        TemplateEngine engine = getTemplateEngine();
        Template template = engine.getTemplate("Namespace.yaml");
        return template.render(params);
    }


    public String renderServiceYamlContent(Map<String, Object> params) {
        TemplateEngine engine = getTemplateEngine();
        Template template = engine.getTemplate("Service.yaml");
        return template.render(params);
    }

    public String renderIngressYamlContent(Map<String, Object> params) {
        TemplateEngine engine = getTemplateEngine();
        Template template = engine.getTemplate("Ingress.yaml");
        return template.render(params);
    }

    public String renderStatefulSetYamlContent(Map<String, Object> params) {
        TemplateEngine engine = getTemplateEngine();
        Template template = engine.getTemplate("StatefulSet.yaml");
        return template.render(params);
    }

    public String renderKruiseStatefulSetYamlContent(Map<String, Object> params) {
        TemplateEngine engine = getTemplateEngine();
        Template template = engine.getTemplate("KruiseStatefulSet.yaml");
        return template.render(params);
    }
}
