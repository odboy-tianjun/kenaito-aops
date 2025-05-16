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
import cn.odboy.app.controller.cmdb.vo.ContainerdSpecConfigCreateArgs;
import cn.odboy.app.controller.cmdb.vo.ContainerdSpecConfigUpdateArgs;
import cn.odboy.app.dal.dataobject.AopsKubernetesContainerdSpecConfigDO;
import cn.odboy.app.dal.mysql.AopsKubernetesContainerdSpecConfigMapper;
import cn.odboy.common.exception.BadRequestException;
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
 * Kubernetes容器规格配置 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Service
@RequiredArgsConstructor
public class AopsKubernetesContainerdSpecConfigServiceImpl implements AopsKubernetesContainerdSpecConfigService {
    private final AopsKubernetesContainerdSpecConfigMapper currentMapper;

    @Override
    public PageResult<AopsKubernetesContainerdSpecConfigDO> describeContainerdSpecConfigPage(PageArgs<AopsKubernetesContainerdSpecConfigDO> args) {
        LambdaQueryWrapper<AopsKubernetesContainerdSpecConfigDO> wrapper = new LambdaQueryWrapper<>();
        AopsKubernetesContainerdSpecConfigDO queryParams = args.getArgs();
        if (queryParams != null) {
            wrapper.eq(StrUtil.isNotBlank(queryParams.getSpecName()), AopsKubernetesContainerdSpecConfigDO::getSpecName, queryParams.getSpecName());
        }
        return AnyQueryTool.selectPageResult(currentMapper, args, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createContainerdSpecConfig(ContainerdSpecConfigCreateArgs args) {
        if (currentMapper.getContainerdSpecConfigCountBySpecName(args.getSpecName()) > 0) {
            throw new BadRequestException("规格名称已存在");
        }
        AopsKubernetesContainerdSpecConfigDO copied = BeanUtil.copyProperties(args, AopsKubernetesContainerdSpecConfigDO.class);
        currentMapper.insert(copied);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContainerdSpecConfig(DeleteByIdArgs args) {
        currentMapper.deleteById(args.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContainerdSpecConfig(ContainerdSpecConfigUpdateArgs args) {
        if (currentMapper.getContainerdSpecConfigCountBySpecNameAndId(args.getSpecName(), args.getId()) > 0) {
            throw new BadRequestException("规格名称已存在");
        }
        AopsKubernetesContainerdSpecConfigDO copied = BeanUtil.copyProperties(args, AopsKubernetesContainerdSpecConfigDO.class);
        currentMapper.updateById(copied);
    }
}
