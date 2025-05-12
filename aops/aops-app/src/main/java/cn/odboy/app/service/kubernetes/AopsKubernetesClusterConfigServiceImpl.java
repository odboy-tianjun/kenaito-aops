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

import cn.hutool.core.util.StrUtil;
import cn.odboy.app.dal.dataobject.AopsKubernetesClusterConfigDO;
import cn.odboy.app.dal.mysql.AopsKubernetesClusterConfigMapper;
import cn.odboy.common.pojo.PageArgs;
import cn.odboy.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public void modifyMetaById(AopsKubernetesClusterConfigDO clusterConfig) {
        aopsKubernetesClusterConfigMapper.updateById(clusterConfig);
    }

    @Override
    public PageResult<AopsKubernetesClusterConfigDO> describeKubernetesClusterConfigPage(PageArgs<AopsKubernetesClusterConfigDO> args) {
        Page<AopsKubernetesClusterConfigDO> pageArgs = new Page<>(args.getPage(), args.getSize());
        LambdaQueryWrapper<AopsKubernetesClusterConfigDO> wrapper = new LambdaQueryWrapper<>();
        AopsKubernetesClusterConfigDO args1 = args.getArgs();
        if (args1 != null) {
            wrapper.like(StrUtil.isNotBlank(args1.getClusterName()), AopsKubernetesClusterConfigDO::getClusterName, args1.getClusterName());
            wrapper.eq(StrUtil.isNotBlank(args1.getEnvCode()), AopsKubernetesClusterConfigDO::getEnvCode, args1.getEnvCode());
        }
        Page<AopsKubernetesClusterConfigDO> pageResult = aopsKubernetesClusterConfigMapper.selectPage(pageArgs, wrapper);
        PageResult<AopsKubernetesClusterConfigDO> result = new PageResult<>();
        result.setContent(pageResult.getRecords());
        result.setTotalElements(pageResult.getTotal());
        return result;
    }
}
