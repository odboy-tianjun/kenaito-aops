package cn.odboy.core.controller.system;

import cn.odboy.base.PageResult;
import cn.odboy.core.api.system.SystemDictDetailApi;
import cn.odboy.core.service.system.dto.CreateDictDetailRequest;
import cn.odboy.core.service.system.dto.QueryDictDetailRequest;
import cn.odboy.core.dal.dataobject.system.DictDetail;
import cn.odboy.core.service.system.SystemDictDetailService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Api(tags = "系统：字典详情管理")
@RequestMapping("/api/dictDetail")
public class DictDetailController {
    private final SystemDictDetailService systemDictDetailService;
    private final SystemDictDetailApi systemDictDetailApi;

    @ApiOperation("查询字典详情")
    @GetMapping
    public ResponseEntity<PageResult<DictDetail>> describeDictDetailPage(QueryDictDetailRequest criteria) {
        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
        return new ResponseEntity<>(systemDictDetailApi.describeDictDetailPage(criteria, page), HttpStatus.OK);
    }

    @ApiOperation("查询多个字典详情")
    @GetMapping(value = "/getDictDetailMaps")
    public ResponseEntity<Object> getDictDetailMaps(@RequestParam String dictName) {
        String[] names = dictName.split("[,，]");
        Map<String, List<DictDetail>> dictMap = new HashMap<>(16);
        for (String name : names) {
            dictMap.put(name, systemDictDetailApi.describeDictDetailListByName(name));
        }
        return new ResponseEntity<>(dictMap, HttpStatus.OK);
    }

    @ApiOperation("新增字典详情")
    @PostMapping(value = "/saveDictDetail")
    @PreAuthorize("@el.check('dict:add')")
    public ResponseEntity<Object> saveDictDetail(@Validated @RequestBody CreateDictDetailRequest resources) {
        systemDictDetailService.saveDictDetail(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("修改字典详情")
    @PostMapping(value = "/modifyDictDetailById")
    @PreAuthorize("@el.check('dict:edit')")
    public ResponseEntity<Object> modifyDictDetailById(@Validated(DictDetail.Update.class) @RequestBody DictDetail resources) {
        systemDictDetailService.modifyDictDetailById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("删除字典详情")
    @PostMapping(value = "/removeDictDetailById")
    @PreAuthorize("@el.check('dict:del')")
    public ResponseEntity<Object> removeDictDetailById(@RequestBody DictDetail args) {
        systemDictDetailService.removeDictDetailById(args.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}