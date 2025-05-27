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
package cn.odboy.app.service.kubernetes;

import cn.odboy.app.controller.vo.NetworkIngressCreateArgs;
import cn.odboy.app.controller.vo.NetworkIngressUpdateArgs;
import cn.odboy.app.dal.dataobject.AopsKubernetesNetworkIngressDO;
import cn.odboy.common.model.PageArgs;
import cn.odboy.common.model.PageResult;
import cn.odboy.common.model.DeleteByIdArgs;

/**
 * <p>
 * Kubernetes网络ingress-nginx 服务类
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
public interface AopsKubernetesNetworkIngressService {

    PageResult<AopsKubernetesNetworkIngressDO> describeIngressPage(PageArgs<AopsKubernetesNetworkIngressDO> args);

    void createIngress(NetworkIngressCreateArgs args);

    void deleteIngress(DeleteByIdArgs args);

    void updateIngress(NetworkIngressUpdateArgs args);
}
