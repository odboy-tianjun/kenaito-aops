package cn.odboy.core.controller.system;

import cn.odboy.base.PageResult;
import cn.odboy.core.api.system.SystemDictApi;
import cn.odboy.core.service.system.dto.CreateDictRequest;
import cn.odboy.core.service.system.dto.QueryDictRequest;
import cn.odboy.core.dal.dataobject.system.Dict;
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
    private final SystemDictApi systemDictApi;
    private final SystemDictService systemDictService;

    @ApiOperation("导出字典数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dict:list')")
    public void exportDict(HttpServletResponse response, QueryDictRequest criteria) throws IOException {
        systemDictService.downloadDictExcel(systemDictApi.describeDictList(criteria), response);
    }

    @ApiOperation("查询字典")
    @PostMapping(value = "/queryAllDict")
    @PreAuthorize("@el.check('dict:list')")
    public ResponseEntity<List<Dict>> queryAllDict() {
        return new ResponseEntity<>(systemDictApi.describeDictList(new QueryDictRequest()), HttpStatus.OK);
    }

    @ApiOperation("查询字典")
    @GetMapping
    @PreAuthorize("@el.check('dict:list')")
    public ResponseEntity<PageResult<Dict>> queryDict(QueryDictRequest criteria) {
        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
        return new ResponseEntity<>(systemDictApi.describeDictPage(criteria, page), HttpStatus.OK);
    }

    @ApiOperation("新增字典")
    @PostMapping(value = "/saveDict")
    @PreAuthorize("@el.check('dict:add')")
    public ResponseEntity<Object> saveDict(@Validated @RequestBody CreateDictRequest resources) {
        systemDictService.saveDict(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("修改字典")
    @PostMapping(value = "/modifyDictById")
    @PreAuthorize("@el.check('dict:edit')")
    public ResponseEntity<Object> modifyDictById(@Validated(Dict.Update.class) @RequestBody Dict resources) {
        systemDictService.modifyDictById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("删除字典")
    @PostMapping(value = "/removeDictByIds")
    @PreAuthorize("@el.check('dict:del')")
    public ResponseEntity<Object> removeDictByIds(@RequestBody Set<Long> ids) {
        systemDictService.removeDictByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}