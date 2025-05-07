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
package cn.odboy.core.framework.mybatisplus.interfaces;

import cn.odboy.base.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 公共抽象Mapper接口类
 *
 * @author odboy
 * @date 2022-04-03
 */
public interface EasyService<T> extends IService<T> {
    <G> int saveFeatureClazz(G resources);

    <G> int saveFeatureClazzList(List<G> resources);

    <G> int modifyFeatureClazzById(G resources);

    <G> boolean modifyFeatureClazzListById(Collection<G> resources, int batchSize);

    <G> G describeFeatureClazzById(Serializable id, Class<G> targetClazz);

    T describeClazzByArgs(LambdaQueryWrapper<T> wrapper, SFunction<T, ?> orderColumn);

    <G> G describeFeatureClazzByArgs(LambdaQueryWrapper<T> wrapper, SFunction<T, ?> orderColumn, Class<G> clazz);

    <G> List<G> describeFeatureClazzListByIds(List<Serializable> ids, Class<G> targetClazz);

    <Q> List<T> describeClazzListByArgs(Q criteria);

    List<T> describeClazzListByArgs(LambdaQueryWrapper<T> wrapper);

    <G, Q> List<G> describeFeatureClazzListByArgs(Q criteria, Class<G> targetClazz);

    <G> List<G> describeFeatureClazzListByArgs(LambdaQueryWrapper<T> wrapper, Class<G> targetClazz);

    PageResult<T> describeClazzPageByArgs(LambdaQueryWrapper<T> wrapper, IPage<T> pageable);

    <G> PageResult<G> describeFeatureClazzPageByArgs(LambdaQueryWrapper<T> wrapper, IPage<T> pageable, Class<G> targetClazz);

    <G, Q> PageResult<G> describeFeatureClazzPageByArgs(Q criteria, IPage<T> pageable, Class<G> targetClazz);

    <Q> PageResult<T> describeClazzPageByArgs(Q criteria, IPage<T> pageable);

    int modifyClazzByArgs(LambdaQueryWrapper<T> wrapper, T entity);

    int removeClazzByArgs(LambdaQueryWrapper<T> wrapper);
}
