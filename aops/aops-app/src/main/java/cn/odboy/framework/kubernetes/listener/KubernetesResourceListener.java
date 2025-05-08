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
package cn.odboy.framework.kubernetes.listener;

import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.fabric8.kubernetes.client.informers.cache.Lister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * K8s资源变动监听
 *
 * @author odboy
 * @date 2025-02-11
 */
@Slf4j
@Component
public class KubernetesResourceListener {
    private static final Map<String, SharedInformerFactory> CLIENT_MAP = new HashMap<>();

    public Lister<Namespace> namespaceLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Namespace.class).getIndexer());
    }

    public Lister<Node> nodeLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Node.class).getIndexer());
    }

    public Lister<Service> serviceLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Service.class).getIndexer());
    }

    public Lister<Ingress> ingressLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Ingress.class).getIndexer());
    }

    public Lister<Pod> podLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Pod.class).getIndexer());
    }

    public Lister<Endpoints> endpointsLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Endpoints.class).getIndexer());
    }

    /**
     * @param clusterCode 集群编码
     * @param content     连接配置
     */
    public void addListener(String clusterCode, String content) {
        Config config = Config.fromKubeconfig(content);
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build()) {
            // 注册 Informer
            SharedInformerFactory sharedInformerFactory = client.informers();
            int reSyncPeriodInMillis = 1000 * 10;
            sharedInformerFactory.sharedIndexInformerFor(Pod.class, reSyncPeriodInMillis);
            sharedInformerFactory.sharedIndexInformerFor(Namespace.class, reSyncPeriodInMillis);
            sharedInformerFactory.sharedIndexInformerFor(Service.class, reSyncPeriodInMillis);
            sharedInformerFactory.sharedIndexInformerFor(Endpoints.class, reSyncPeriodInMillis);
            sharedInformerFactory.sharedIndexInformerFor(Node.class, reSyncPeriodInMillis);
            sharedInformerFactory.sharedIndexInformerFor(Ingress.class, reSyncPeriodInMillis);
            // 启动所有注册的 Informer
            sharedInformerFactory.startAllRegisteredInformers();
            CLIENT_MAP.put(clusterCode, sharedInformerFactory);
            log.info("集群 {} 初始化Informer成功", clusterCode);
        } catch (Exception e) {
            log.info("集群 {} 初始化Informer失败", clusterCode, e);
        }
    }
}
