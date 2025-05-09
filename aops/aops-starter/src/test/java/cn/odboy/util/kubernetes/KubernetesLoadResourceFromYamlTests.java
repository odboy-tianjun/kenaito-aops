package cn.odboy.util.kubernetes;

import cn.hutool.core.lang.Dict;
import cn.odboy.app.framework.kubernetes.core.context.KubernetesYamlTemplateManager;
import cn.odboy.app.framework.kubernetes.core.vo.ArgsClusterCodeVo;
import cn.odboy.app.framework.kubernetes.core.vo.ArgsDryRunVo;
import cn.odboy.app.framework.kubernetes.core.vo.ArgsYamlVo;
import cn.odboy.app.framework.kubernetes.core.repository.KubernetesIngressRepository;
import cn.odboy.app.framework.kubernetes.core.repository.KubernetesNamespaceRepository;
import cn.odboy.app.framework.kubernetes.core.repository.KubernetesOpenKruiseStatefulSetRepository;
import cn.odboy.app.framework.kubernetes.core.repository.KubernetesServiceRepository;
import cn.odboy.app.framework.kubernetes.core.repository.KubernetesStatefulSetRepository;
import cn.odboy.app.framework.kubernetes.core.util.KubernetesResourceNameUtil;
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
public class KubernetesLoadResourceFromYamlTests {
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
    @Autowired
    private KubernetesYamlTemplateManager kubernetesYamlTemplateManager;

    @Test
    @SneakyThrows
    public void testNamespace() {
        String appName = "demo";
        String clusterCode = "local_daily";
        String content = kubernetesYamlTemplateManager.renderNamespaceYamlContent(Dict.create()
                .set("appName", appName)
        );
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
        String clusterCode = "local_daily";
        String serviceName = KubernetesResourceNameUtil.getServiceName(appName, envCode);
        String content = kubernetesYamlTemplateManager.renderServiceYamlContent(Dict.create()
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
        String clusterCode = "local_daily";
        String envCode = "daily";
        String serviceName = KubernetesResourceNameUtil.getServiceName(appName, envCode);
        String content = kubernetesYamlTemplateManager.renderIngressYamlContent(Dict.create()
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
        String clusterCode = "local_daily";
        String envCode = "daily";
        String podName = KubernetesResourceNameUtil.getPodName(appName, envCode);
        Integer replicas = 2;
        String appVersion = "online_20250116";
        String content = kubernetesYamlTemplateManager.renderStatefulSetYamlContent(Dict.create()
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
        String clusterCode = "local_daily";
        String envCode = "daily";
        String podName = KubernetesResourceNameUtil.getPodName(appName, envCode);
        Integer replicas = 2;
        String appVersion = "online_20250116";
        String content = kubernetesYamlTemplateManager.renderKruiseStatefulSetYamlContent(Dict.create()
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
