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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.app.controller.cmdb.vo.ClusterConfigCreateArgs;
import cn.odboy.app.controller.cmdb.vo.ClusterConfigModifyDefaultAppYmlArgs;
import cn.odboy.app.controller.cmdb.vo.ClusterConfigUpdateArgs;
import cn.odboy.app.dal.dataobject.AopsKubernetesClusterConfigDO;
import cn.odboy.app.dal.mysql.AopsKubernetesClusterConfigMapper;
import cn.odboy.common.pojo.PageArgs;
import cn.odboy.common.pojo.PageResult;
import cn.odboy.common.pojo.vo.DeleteByIdArgs;
import cn.odboy.core.framework.mybatisplus.mybatis.core.util.AnyQueryTool;
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
    private final AopsKubernetesClusterConfigMapper currentMapper;

    @Override
    public List<AopsKubernetesClusterConfigDO> describeClusterConfigList() {
        return currentMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(AopsKubernetesClusterConfigDO clusterConfig) {
        currentMapper.updateById(clusterConfig);
    }

    @Override
    public PageResult<AopsKubernetesClusterConfigDO> describeClusterConfigPage(PageArgs<AopsKubernetesClusterConfigDO> args) {
        LambdaQueryWrapper<AopsKubernetesClusterConfigDO> wrapper = new LambdaQueryWrapper<>();
        AopsKubernetesClusterConfigDO queryParams = args.getArgs();
        if (queryParams != null) {
            wrapper.like(StrUtil.isNotBlank(queryParams.getClusterName()), AopsKubernetesClusterConfigDO::getClusterName, queryParams.getClusterName());
            wrapper.eq(StrUtil.isNotBlank(queryParams.getEnvCode()), AopsKubernetesClusterConfigDO::getEnvCode, queryParams.getEnvCode());
        }
        PageResult<AopsKubernetesClusterConfigDO> result = AnyQueryTool.selectPageResult(currentMapper, args, wrapper);
        // 解决前端YamlEdit报错
        result.getContent().forEach(c -> {
            if (StrUtil.isBlank(c.getClusterDefaultAppYaml())) {
                c.setClusterDefaultAppYaml("");
            }
        });
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyClusterDefaultAppYml(ClusterConfigModifyDefaultAppYmlArgs args) {
        AopsKubernetesClusterConfigDO aopsKubernetesClusterConfigDO = new AopsKubernetesClusterConfigDO();
        aopsKubernetesClusterConfigDO.setId(args.getId());
        aopsKubernetesClusterConfigDO.setClusterDefaultAppYaml(args.getClusterDefaultAppYaml());
        currentMapper.updateById(aopsKubernetesClusterConfigDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateClusterConfig(ClusterConfigUpdateArgs args) {
        AopsKubernetesClusterConfigDO copied = BeanUtil.copyProperties(args, AopsKubernetesClusterConfigDO.class);
        currentMapper.updateById(copied);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteClusterConfig(DeleteByIdArgs args) {
        currentMapper.deleteById(args.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createClusterConfig(ClusterConfigCreateArgs args) {
        AopsKubernetesClusterConfigDO copied = BeanUtil.copyProperties(args, AopsKubernetesClusterConfigDO.class);
        currentMapper.insert(copied);
    }
}
