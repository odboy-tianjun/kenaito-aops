/*
 *  Copyright 2021-2025 Tian Jun
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
package cn.odboy.service.impl;

import cn.odboy.constant.GlobalEnvEnum;
import cn.odboy.dal.dataobject.AopsKubernetesClusterNode;
import cn.odboy.dal.mysql.AopsKubernetesClusterNodeMapper;
import cn.odboy.service.AopsKubernetesClusterNodeService;
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
    public AopsKubernetesClusterNode describeKubernetesClusterNodeByArgs(GlobalEnvEnum envEnum, String nodeInternalIp) {
        return aopsKubernetesClusterNodeMapper.selectOne(new LambdaQueryWrapper<AopsKubernetesClusterNode>()
                .eq(AopsKubernetesClusterNode::getEnvCode, envEnum.getCode())
                .eq(AopsKubernetesClusterNode::getNodeInternalIp, nodeInternalIp)
        );
    }

    @Override
    public void saveBatch(List<AopsKubernetesClusterNode> newRecordList, int batchSize) {
        aopsKubernetesClusterNodeMapper.insert(newRecordList, batchSize);
    }

    @Override
    public void updateBatchById(List<AopsKubernetesClusterNode> updRecordList, int batchSize) {
        aopsKubernetesClusterNodeMapper.updateById(updRecordList, batchSize);
    }
}
