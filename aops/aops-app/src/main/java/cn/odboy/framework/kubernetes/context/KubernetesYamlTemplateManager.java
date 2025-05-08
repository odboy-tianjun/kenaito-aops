package cn.odboy.framework.kubernetes.context;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.odboy.framework.kubernetes.config.KubernetesProperties;
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
