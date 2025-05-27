package cn.odboy.core.controller.system;

import cn.odboy.common.model.PageResult;
import cn.odboy.core.dal.dataobject.system.DictDO;
import cn.odboy.core.framework.operalog.annotaions.OperationLog;
import cn.odboy.core.service.system.dto.DictCreateArgs;
import cn.odboy.core.service.system.dto.DictQueryArgs;
import cn.odboy.core.service.system.SystemDictService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Api(tags = "系统：字典管理")
@RequestMapping("/api/dict")
public class DictController {
    private final SystemDictService systemDictService;

    @OperationLog
    @ApiOperation("导出字典数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dict:list')")
    public void exportDict(HttpServletResponse response, DictQueryArgs args) throws IOException {
        systemDictService.downloadDictExcel(systemDictService.describeDictList(args), response);
    }

    @ApiOperation("查询字典")
    @PostMapping(value = "/queryAllDict")
    @PreAuthorize("@el.check('dict:list')")
    public ResponseEntity<List<DictDO>> queryAllDict() {
        return new ResponseEntity<>(systemDictService.describeDictList(new DictQueryArgs()), HttpStatus.OK);
    }

    @ApiOperation("查询字典")
    @GetMapping
    @PreAuthorize("@el.check('dict:list')")
    public ResponseEntity<PageResult<DictDO>> queryDict(DictQueryArgs args) {
        Page<Object> page = new Page<>(args.getPage(), args.getSize());
        return new ResponseEntity<>(systemDictService.describeDictPage(args, page), HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("新增字典")
    @PostMapping(value = "/saveDict")
    @PreAuthorize("@el.check('dict:add')")
    public ResponseEntity<Object> saveDict(@Validated @RequestBody DictCreateArgs args) {
        systemDictService.saveDict(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @OperationLog
    @ApiOperation("修改字典")
    @PostMapping(value = "/modifyDictById")
    @PreAuthorize("@el.check('dict:edit')")
    public ResponseEntity<Object> modifyDictById(@Validated(DictDO.Update.class) @RequestBody DictDO resources) {
        systemDictService.modifyDictById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @OperationLog
    @ApiOperation("删除字典")
    @PostMapping(value = "/removeDictByIds")
    @PreAuthorize("@el.check('dict:del')")
    public ResponseEntity<Object> removeDictByIds(@RequestBody Set<Long> ids) {
        systemDictService.removeDictByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
