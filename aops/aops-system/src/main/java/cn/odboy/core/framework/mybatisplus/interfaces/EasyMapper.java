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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.odboy.base.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公共抽象Mapper接口类
 *
 * @author odboy
 * @date 2022-04-03
 */
public interface EasyMapper<E> extends BaseMapper<E> {
    /**
     * 批量插入
     *
     * @param entityList List<E>
     * @return 影响的行数
     */
    int insertBatchSomeColumn(List<E> entityList);

    /**
     * 根据id全量更新
     *
     * @param entity E
     * @return 影响的行数
     */
    int alwaysUpdateSomeColumnById(@Param("et") E entity);

    /**
     * 获取字典
     *
     * @param wrapper   LambdaQueryWrapper<E>
     * @param keyName   键对应的列名称
     * @param valueName 值对应的列名称
     * @return Map<String, Object>
     */
    default Map<String, Object> getMapWithKv(LambdaQueryWrapper<E> wrapper, String keyName, String valueName) {
        Map<String, Object> result = new ConcurrentHashMap<>(1);
        List<E> list = this.selectList(wrapper);
        if (CollUtil.isEmpty(list)) {
            return new HashMap<>(3);
        }
        E tempObj = list.stream().findFirst().orElse(null);
        if (tempObj == null) {
            return new HashMap<>(3);
        }
        Field[] fields = ReflectUtil.getFields(tempObj.getClass());
        for (E e : list) {
            Field keyField = null;
            Field valueField = null;
            for (Field field : fields) {
                String fieldName = field.getName();
                if (fieldName.equals(keyName)) {
                    keyField = field;
                } else if (fieldName.equals(valueName)) {
                    valueField = field;
                    break;
                }
            }
            String fieldKey = String.valueOf(ReflectUtil.getFieldValue(e, keyField));
            Object fieldValue = ReflectUtil.getFieldValue(e, valueField);
            if (fieldKey != null) {
                result.put(fieldKey, fieldValue);
            }
        }
        return result;
    }

    /**
     * 条件查询列表, 并返回期望的对象
     *
     * @param wrapper LambdaQueryWrapper<E>
     * @param clazz   期望的对象类型
     * @return List<T>
     */
    default <T> List<T> queryFeatureClazzList(LambdaQueryWrapper<E> wrapper, Class<T> clazz) {
        List<E> list = this.selectList(wrapper);
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return BeanUtil.copyToList(list, clazz);
    }

    /**
     * 条件查询列表, 并返回期望的对象
     *
     * @param wrapper LambdaQueryWrapper<E>
     * @param clazz   期望的对象类型
     * @return List<T>
     */
    default <T> List<T> queryFeatureClazzList(LambdaQueryChainWrapper<E> wrapper, Class<T> clazz) {
        List<E> list = wrapper.list();
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return BeanUtil.copyToList(list, clazz);
    }

    /**
     * 条件查询分页列表, 并返回期望的对象
     *
     * @param pageable 分页参数
     * @param wrapper  LambdaQueryWrapper<E> wrapper
     * @param clazz    期望的对象类型
     * @return IPage<T>
     */
    default <T> PageResult<T> queryFeatureClazzPage(Pageable pageable, LambdaQueryWrapper<E> wrapper, Class<T> clazz) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        pageNumber = pageNumber <= 0 ? 1 : pageNumber;
        pageSize = pageSize <= 0 ? 10 : pageSize;
        IPage<E> pageInfo = this.selectPage(new Page<>(pageNumber, pageSize), wrapper);
        if (CollUtil.isEmpty(pageInfo.getRecords())) {
            return new PageResult<>(new ArrayList<>(), pageInfo.getTotal());
        }
        return new PageResult<>(BeanUtil.copyToList(pageInfo.getRecords(), clazz), pageInfo.getTotal());
    }

    /**
     * 条件查询分页列表, 并返回期望的对象
     *
     * @param pageable 分页参数
     * @param wrapper  LambdaQueryWrapper<E> wrapper
     * @param clazz    期望的对象类型
     * @return IPage<T>
     */
    default <T> PageResult<T> queryFeatureClazzPage(Pageable pageable, LambdaQueryChainWrapper<E> wrapper, Class<T> clazz) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        pageNumber = pageNumber <= 0 ? 1 : pageNumber;
        pageSize = pageSize <= 0 ? 10 : pageSize;
        IPage<E> pageInfo = wrapper.page(new Page<>(pageNumber, pageSize));
        if (CollUtil.isEmpty(pageInfo.getRecords())) {
            return new PageResult<>(new ArrayList<>(), pageInfo.getTotal());
        }
        return new PageResult<>(BeanUtil.copyToList(pageInfo.getRecords(), clazz), pageInfo.getTotal());
    }

    /**
     * 获取目标类
     *
     * @param wrapper LambdaQueryChainWrapper<E> wrapper
     * @param clazz   期望的对象类型
     * @return T
     */
    default <T> T getFeatureClazz(LambdaQueryChainWrapper<E> wrapper, Class<T> clazz) {
        E e = this.selectOne(wrapper);
        if (e == null) {
            return null;
        }
        return BeanUtil.copyProperties(e, clazz);
    }
}
