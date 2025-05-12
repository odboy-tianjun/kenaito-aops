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

import cn.odboy.app.dal.dataobject.AopsKubernetesClusterConfigDO;
import cn.odboy.common.pojo.PageArgs;
import cn.odboy.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * <p>
 * Kubernetes集群配置 服务类
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
public interface AopsKubernetesClusterConfigService {
    List<AopsKubernetesClusterConfigDO> describeKubernetesClusterConfig();

    void modifyMetaById(AopsKubernetesClusterConfigDO clusterConfig);

    PageResult<AopsKubernetesClusterConfigDO> describeKubernetesClusterConfigPage(PageArgs<AopsKubernetesClusterConfigDO> args);
}
