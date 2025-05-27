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
package cn.odboy.app.service.app;

import cn.hutool.core.bean.BeanUtil;
import cn.odboy.app.controller.vo.AopsProductLineVo;
import cn.odboy.app.controller.vo.ProductLineCreateArgs;
import cn.odboy.app.controller.vo.ProductLineUpdateArgs;
import cn.odboy.app.dal.dataobject.AopsProductLineDO;
import cn.odboy.app.dal.mysql.AopsProductLineMapper;
import cn.odboy.common.model.DeleteByIdArgs;
import cn.odboy.common.model.PageArgs;
import cn.odboy.common.model.PageResult;
import cn.odboy.common.model.TreeNodeResult;
import cn.odboy.common.util.CollUtil;
import cn.odboy.core.framework.mybatisplus.mybatis.core.util.AnyQueryTool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 产品 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Service
@RequiredArgsConstructor
public class AopsProductLineServiceImpl implements AopsProductLineService {
    private final AopsProductLineMapper currentMapper;
    private final AopsAppService aopsAppService;

    @Override
    public List<TreeNodeResult> describeProductLineTree() {
        Map<Long, TreeNodeResult> idLineMap = new LinkedHashMap<>(1);
        List<AopsProductLineDO> allLineList = currentMapper.selectList(new LambdaQueryWrapper<AopsProductLineDO>()
                .orderByAsc(AopsProductLineDO::getId)
        );
        for (AopsProductLineDO aopsProductLineDO : allLineList) {
            TreeNodeResult treeNodeResult = new TreeNodeResult();
            treeNodeResult.setId(aopsProductLineDO.getId());
            treeNodeResult.setLabel(aopsProductLineDO.getLineName());
            treeNodeResult.setChildren(new ArrayList<>());
            treeNodeResult.setParentId(aopsProductLineDO.getParentId());
            treeNodeResult.setExt1(aopsAppService.getAppCountByProductLineId(aopsProductLineDO.getId()) > 0);
            idLineMap.put(aopsProductLineDO.getId(), treeNodeResult);
        }
        for (AopsProductLineDO aopsProductLineDO : allLineList) {
            if (aopsProductLineDO.getParentId() != 0) {
                TreeNodeResult parentNode = idLineMap.getOrDefault(aopsProductLineDO.getParentId(), null);
                if (parentNode != null) {
                    TreeNodeResult treeNodeResult = new TreeNodeResult();
                    treeNodeResult.setId(aopsProductLineDO.getId());
                    treeNodeResult.setLabel(aopsProductLineDO.getLineName());
                    treeNodeResult.setChildren(new ArrayList<>());
                    treeNodeResult.setParentId(parentNode.getId());
                    treeNodeResult.setExt1(aopsAppService.getAppCountByProductLineId(parentNode.getId()) > 0);
                    parentNode.getChildren().add(treeNodeResult);
                }
            }
        }
        TreeNodeResult finalNodeResult = new TreeNodeResult();
        finalNodeResult.setId(0L);
        finalNodeResult.setLabel("全部产品线");
        finalNodeResult.setChildren(idLineMap.values()
                .stream()
                .filter(f -> f.getParentId() == 0)
                .collect(Collectors.toList()));
        finalNodeResult.setParentId(null);
        return CollUtil.newArrayList(finalNodeResult);
    }

    @Override
    public PageResult<AopsProductLineVo> describeProductLinePage(PageArgs<AopsProductLineDO> args) {
        LambdaQueryWrapper<AopsProductLineDO> wrapper = new LambdaQueryWrapper<>();
        AopsProductLineDO queryParams = args.getArgs();
        if (queryParams != null) {
            wrapper.eq(queryParams.getParentId() != null, AopsProductLineDO::getParentId, queryParams.getParentId());
        }
        PageResult<AopsProductLineVo> pageResult = AnyQueryTool.selectPageResult(currentMapper, args, wrapper, AopsProductLineVo.class);
        pageResult.getContent().forEach(c -> {
            c.setHasApp(aopsAppService.getAppCountByProductLineId(c.getId()) > 0);
            c.setLinePathName(getProductLinePath(c.getId()));
        });
        return pageResult;
    }

    private String getProductLinePath(Long productLineId) {
        if (productLineId == null || productLineId == 0L) {
            return "";
        }
        // 递归获取所有父级名称
        List<String> pathNames = new ArrayList<>();
        Long currentId = productLineId;

        while (currentId != null && currentId != 0L) {
            AopsProductLineDO current = currentMapper.selectById(currentId);
            if (current == null) {
                break;
            }
            pathNames.add(current.getLineName());
            currentId = current.getParentId();
        }
        // 反转顺序（从顶级到当前）
        Collections.reverse(pathNames);
        return String.join("/", pathNames);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProductLine(ProductLineCreateArgs args) {
        AopsProductLineDO copied = BeanUtil.copyProperties(args, AopsProductLineDO.class);
        currentMapper.insert(copied);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductLine(DeleteByIdArgs args) {
        currentMapper.deleteById(args.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductLine(ProductLineUpdateArgs args) {
        AopsProductLineDO copied = BeanUtil.copyProperties(args, AopsProductLineDO.class);
        currentMapper.updateById(copied);
    }
}
