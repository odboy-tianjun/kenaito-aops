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
package cn.odboy.app.controller;

import cn.odboy.app.controller.vo.AopsProductLineVo;
import cn.odboy.app.controller.vo.ProductLineCreateArgs;
import cn.odboy.app.controller.vo.ProductLineUpdateArgs;
import cn.odboy.app.dal.dataobject.AopsProductLineDO;
import cn.odboy.app.service.app.AopsProductLineService;
import cn.odboy.common.model.DeleteByIdArgs;
import cn.odboy.common.model.PageArgs;
import cn.odboy.common.model.PageResult;
import cn.odboy.common.model.TreeNodeResult;
import cn.odboy.core.framework.operalog.annotaions.OperationLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Api(tags = "产品管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cmdb/productLine")
public class AopsCmdbProductLineController {
    private final AopsProductLineService currentService;

    @ApiOperation("查询产品树")
    @PostMapping("/describeProductLineTree")
    public ResponseEntity<List<TreeNodeResult>> describeProductLineTree() {
        return ResponseEntity.ok(currentService.describeProductLineTree());
    }

    @ApiOperation("分页查询产品列表")
    @PostMapping("/describeProductLinePage")
    public ResponseEntity<PageResult<AopsProductLineVo>> describeProductLinePage(@Validated @RequestBody PageArgs<AopsProductLineDO> args) {
        return ResponseEntity.ok(currentService.describeProductLinePage(args));
    }

    @OperationLog
    @ApiOperation("创建产品")
    @PostMapping("/createProductLine")
    public ResponseEntity<Void> createProductLine(@Validated @RequestBody ProductLineCreateArgs args) {
        currentService.createProductLine(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("删除产品")
    @PostMapping("/deleteProductLine")
    public ResponseEntity<Void> deleteProductLine(@Validated @RequestBody DeleteByIdArgs args) {
        currentService.deleteProductLine(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("更新产品")
    @PostMapping("/updateProductLine")
    public ResponseEntity<Void> updateProductLine(@Validated @RequestBody ProductLineUpdateArgs args) {
        currentService.updateProductLine(args);
        return ResponseEntity.ok().build();
    }
}
