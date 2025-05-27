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
import cn.odboy.app.controller.vo.NetworkServiceCreateArgs;
import cn.odboy.app.controller.vo.NetworkServiceUpdateArgs;
import cn.odboy.app.dal.dataobject.AopsKubernetesNetworkServiceDO;
import cn.odboy.app.dal.mysql.AopsKubernetesNetworkServiceMapper;
import cn.odboy.common.model.PageArgs;
import cn.odboy.common.model.PageResult;
import cn.odboy.common.model.DeleteByIdArgs;
import cn.odboy.core.framework.mybatisplus.mybatis.core.util.AnyQueryTool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Kubernetes网络service 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Service
@RequiredArgsConstructor
public class AopsKubernetesNetworkServiceServiceImpl implements AopsKubernetesNetworkServiceService {
    private final AopsKubernetesNetworkServiceMapper currentMapper;

    @Override
    public PageResult<AopsKubernetesNetworkServiceDO> describeServicePage(PageArgs<AopsKubernetesNetworkServiceDO> args) {
        LambdaQueryWrapper<AopsKubernetesNetworkServiceDO> wrapper = new LambdaQueryWrapper<>();
        AopsKubernetesNetworkServiceDO queryParams = args.getArgs();
        if (queryParams != null) {
            wrapper.like(StrUtil.isNotBlank(queryParams.getRemark()), AopsKubernetesNetworkServiceDO::getRemark, queryParams.getRemark());
        }
        return AnyQueryTool.selectPageResult(currentMapper, args, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createService(NetworkServiceCreateArgs args) {
        AopsKubernetesNetworkServiceDO copied = BeanUtil.copyProperties(args, AopsKubernetesNetworkServiceDO.class);
        currentMapper.insert(copied);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteService(DeleteByIdArgs args) {
        currentMapper.deleteById(args.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateService(NetworkServiceUpdateArgs args) {
        AopsKubernetesNetworkServiceDO copied = BeanUtil.copyProperties(args, AopsKubernetesNetworkServiceDO.class);
        currentMapper.updateById(copied);
    }
}
