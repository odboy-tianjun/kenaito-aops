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
import cn.odboy.app.dal.mysql.AopsKubernetesClusterConfigMapper;
import cn.odboy.app.framework.kubernetes.core.constant.KubernetesResourceHealthStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * <p>
 * Kubernetes集群配置 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Service
@RequiredArgsConstructor
public class AopsKubernetesClusterConfigServiceImpl implements AopsKubernetesClusterConfigService {
    private final AopsKubernetesClusterConfigMapper aopsKubernetesClusterConfigMapper;

    @Override
    public List<AopsKubernetesClusterConfigDO> describeKubernetesClusterConfig() {
        return aopsKubernetesClusterConfigMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyStatusById(Long id, KubernetesResourceHealthStatusEnum healthStatusEnum) {
        AopsKubernetesClusterConfigDO record = new AopsKubernetesClusterConfigDO();
        record.setId(id);
        record.setStatus(healthStatusEnum.getCode());
        aopsKubernetesClusterConfigMapper.updateById(record);
    }

    @Override
    public List<AopsKubernetesClusterConfigDO> describeKubernetesClusterConfigWithHealth() {
        return aopsKubernetesClusterConfigMapper.selectList(new LambdaQueryWrapper<AopsKubernetesClusterConfigDO>()
                .eq(AopsKubernetesClusterConfigDO::getStatus, KubernetesResourceHealthStatusEnum.HEALTH.getCode())
        );
    }
}
