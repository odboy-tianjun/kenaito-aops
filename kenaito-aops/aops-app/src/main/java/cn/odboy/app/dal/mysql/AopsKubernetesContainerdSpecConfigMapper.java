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
package cn.odboy.app.dal.mysql;

import cn.odboy.app.dal.dataobject.AopsKubernetesContainerdSpecConfigDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Kubernetes容器规格配置 Mapper 接口
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Mapper
public interface AopsKubernetesContainerdSpecConfigMapper extends BaseMapper<AopsKubernetesContainerdSpecConfigDO> {
    default long getContainerdSpecConfigCountBySpecName(String specName) {
        return selectCount(new LambdaQueryWrapper<AopsKubernetesContainerdSpecConfigDO>()
                .eq(AopsKubernetesContainerdSpecConfigDO::getSpecName, specName)
        );
    }

    default long getContainerdSpecConfigCountBySpecNameAndId(String specName, Long id) {
        return selectCount(new LambdaQueryWrapper<AopsKubernetesContainerdSpecConfigDO>()
                .eq(AopsKubernetesContainerdSpecConfigDO::getSpecName, specName)
                .ne(AopsKubernetesContainerdSpecConfigDO::getId, id)
        );
    }
}
