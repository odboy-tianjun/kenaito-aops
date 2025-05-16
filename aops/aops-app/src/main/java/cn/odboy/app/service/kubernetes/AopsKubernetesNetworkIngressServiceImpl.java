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
import cn.odboy.app.controller.cmdb.vo.NetworkIngressCreateArgs;
import cn.odboy.app.controller.cmdb.vo.NetworkIngressUpdateArgs;
import cn.odboy.app.dal.dataobject.AopsKubernetesNetworkIngressDO;
import cn.odboy.app.dal.mysql.AopsKubernetesNetworkIngressMapper;
import cn.odboy.common.pojo.PageArgs;
import cn.odboy.common.pojo.PageResult;
import cn.odboy.common.pojo.vo.DeleteByIdArgs;
import cn.odboy.core.framework.mybatisplus.mybatis.core.util.AnyQueryTool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Kubernetes网络ingress-nginx 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Service
@RequiredArgsConstructor
public class AopsKubernetesNetworkIngressServiceImpl implements AopsKubernetesNetworkIngressService {
    private final AopsKubernetesNetworkIngressMapper currentMapper;

    @Override
    public PageResult<AopsKubernetesNetworkIngressDO> describeIngressPage(PageArgs<AopsKubernetesNetworkIngressDO> args) {
        LambdaQueryWrapper<AopsKubernetesNetworkIngressDO> wrapper = new LambdaQueryWrapper<>();
        AopsKubernetesNetworkIngressDO queryParams = args.getArgs();
        if (queryParams != null) {
            wrapper.like(StrUtil.isNotBlank(queryParams.getRemark()), AopsKubernetesNetworkIngressDO::getRemark, queryParams.getRemark());
        }
        return AnyQueryTool.selectPageResult(currentMapper, args, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createIngress(NetworkIngressCreateArgs args) {
        AopsKubernetesNetworkIngressDO copied = BeanUtil.copyProperties(args, AopsKubernetesNetworkIngressDO.class);
        currentMapper.insert(copied);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIngress(DeleteByIdArgs args) {
        currentMapper.deleteById(args.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIngress(NetworkIngressUpdateArgs args) {
        AopsKubernetesNetworkIngressDO copied = BeanUtil.copyProperties(args, AopsKubernetesNetworkIngressDO.class);
        currentMapper.updateById(copied);
    }
}
