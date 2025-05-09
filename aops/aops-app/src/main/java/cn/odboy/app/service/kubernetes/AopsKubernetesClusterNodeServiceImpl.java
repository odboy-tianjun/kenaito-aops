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

import cn.odboy.common.constant.GlobalEnvEnum;
import cn.odboy.app.dal.dataobject.AopsKubernetesClusterNodeDO;
import cn.odboy.app.dal.mysql.AopsKubernetesClusterNodeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 * Kubernetes集群node节点 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Service
@RequiredArgsConstructor
public class AopsKubernetesClusterNodeServiceImpl implements AopsKubernetesClusterNodeService {
    private final AopsKubernetesClusterNodeMapper aopsKubernetesClusterNodeMapper;

    @Override
    public AopsKubernetesClusterNodeDO describeKubernetesClusterNodeByArgs(GlobalEnvEnum envEnum, String nodeInternalIp) {
        return aopsKubernetesClusterNodeMapper.selectOne(new LambdaQueryWrapper<AopsKubernetesClusterNodeDO>()
                .eq(AopsKubernetesClusterNodeDO::getEnvCode, envEnum.getCode())
                .eq(AopsKubernetesClusterNodeDO::getNodeInternalIp, nodeInternalIp)
        );
    }

    @Override
    public void saveBatch(List<AopsKubernetesClusterNodeDO> newRecordList, int batchSize) {
        aopsKubernetesClusterNodeMapper.insert(newRecordList, batchSize);
    }

    @Override
    public void updateBatchById(List<AopsKubernetesClusterNodeDO> updRecordList, int batchSize) {
        aopsKubernetesClusterNodeMapper.updateById(updRecordList, batchSize);
    }
}
