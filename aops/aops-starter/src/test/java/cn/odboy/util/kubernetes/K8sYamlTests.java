package cn.odboy.util.kubernetes;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.odboy.framework.kubernetes.model.vo.ArgsClusterCodeVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsDryRunVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsYamlVo;
import cn.odboy.framework.kubernetes.repository.KubernetesIngressRepository;
import cn.odboy.framework.kubernetes.repository.KubernetesNamespaceRepository;
import cn.odboy.framework.kubernetes.repository.KubernetesOpenKruiseStatefulSetRepository;
import cn.odboy.framework.kubernetes.repository.KubernetesServiceRepository;
import cn.odboy.framework.kubernetes.repository.KubernetesStatefulSetRepository;
import cn.odboy.framework.kubernetes.util.KubernetesResourceNameUtil;
import io.kubernetes.client.openapi.models.V1Ingress;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.util.Yaml;
import io.openkruise.client.models.KruiseAppsV1alpha1StatefulSet;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * k8sYaml载入 测试
 *
 * @author odboy
 * @date 2025-01-16
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class K8sYamlTests {
    @Autowired
    private KubernetesNamespaceRepository kubernetesNamespaceRepository;
    @Autowired
    private KubernetesServiceRepository kubernetesServiceRepository;
    @Autowired
    private KubernetesIngressRepository kubernetesIngressRepository;
    @Autowired
    private KubernetesStatefulSetRepository kubernetesStatefulSetRepository;
    @Autowired
    private KubernetesOpenKruiseStatefulSetRepository kubernetesOpenKruiseStatefulSetRepository;

    @Test
    @SneakyThrows
    public void testNamespace() {
        String appName = "demo";
        String clusterCode = "local";
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("kubernetes/templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("Namespace.yaml");
        String content = template.render(Dict.create().set("appName", appName));
        kubernetesNamespaceRepository.loadNamespaceFromYaml(
                new ArgsClusterCodeVo(clusterCode),
                new ArgsDryRunVo(false),
                new ArgsYamlVo<>(content, V1Namespace.class)
        );
    }

    @Test
    @SneakyThrows
    public void testService() {
        String appName = "demo";
        String envCode = "daily";
        String clusterCode = "local";
        String serviceName = KubernetesResourceNameUtil.getServiceName(appName, envCode);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("kubernetes/templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("Service.yaml");
        String content = template.render(Dict.create()
                .set("appName", appName)
                .set("envCode", envCode)
                .set("serviceName", serviceName)
        );
        kubernetesServiceRepository.loadServiceFromYaml(
                new ArgsClusterCodeVo(clusterCode),
                new ArgsDryRunVo(false),
                new ArgsYamlVo<>(content, V1Service.class)
        );
    }


    @Test
    @SneakyThrows
    public void testIngress() {
        String appName = "demo";
        String clusterCode = "local";
        String envCode = "daily";
        String serviceName = KubernetesResourceNameUtil.getServiceName(appName, envCode);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("kubernetes/templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("Ingress.yaml");
        String content = template.render(Dict.create()
                .set("appName", appName)
                .set("envCode", envCode)
                .set("serviceName", serviceName)
                .set("hostname", "kenaito-demo.odboy.com")
        );
        kubernetesIngressRepository.loadIngressFromYaml(
                new ArgsClusterCodeVo(clusterCode),
                new ArgsDryRunVo(false),
                new ArgsYamlVo<>(content, V1Ingress.class)
        );
    }


    @Test
    @SneakyThrows
    public void testStatefulSet() {
        String appName = "demo";
        String clusterCode = "local";
        String envCode = "daily";
        String podName = KubernetesResourceNameUtil.getPodName(appName, envCode);
        Integer replicas = 2;
        String appVersion = "online_20250116";
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("kubernetes/templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("StatefulSet.yaml");
        String content = template.render(Dict.create()
                .set("appName", appName)
                .set("envCode", envCode)
                .set("podName", podName)
                .set("replicas", replicas)
                .set("appVersion", appVersion)
        );
        kubernetesStatefulSetRepository.loadStatefulSetFromYaml(
                new ArgsClusterCodeVo(clusterCode),
                new ArgsDryRunVo(false),
                new ArgsYamlVo<>(content, V1StatefulSet.class)
        );
    }

    @Test
    @SneakyThrows
    public void testKruiseStatefulSet() {
        String appName = "demo";
        String clusterCode = "local";
        String envCode = "daily";
        String podName = KubernetesResourceNameUtil.getPodName(appName, envCode);
        Integer replicas = 2;
        String appVersion = "online_20250116";
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("kubernetes/templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("KruiseStatefulSet.yaml");
        String content = template.render(Dict.create()
                .set("appName", appName)
                .set("envCode", envCode)
                .set("podName", podName)
                .set("replicas", replicas)
                .set("appVersion", appVersion)
        );
        kubernetesOpenKruiseStatefulSetRepository.loadStatefulSetFromYaml(
                new ArgsClusterCodeVo(clusterCode),
                new ArgsDryRunVo(false),
                new ArgsYamlVo<>(content, KruiseAppsV1alpha1StatefulSet.class)
        );
    }

    public static void main(String[] args) {
        V1Service v1Service = Yaml.loadAs("xxasdasdas: ", V1Service.class);
        System.err.println(v1Service);
    }
}
