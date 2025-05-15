package cn.odboy.core.controller.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.dal.dataobject.system.DictDetailDO;
import cn.odboy.core.framework.operalog.annotaions.OperationLog;
import cn.odboy.core.service.system.SystemDictDetailService;
import cn.odboy.core.service.system.dto.CreateDictDetailArgs;
import cn.odboy.core.service.system.dto.QueryDictDetailArgs;
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

    @ApiOperation("查询字典详情")
    @GetMapping
    public ResponseEntity<PageResult<DictDetailDO>> describeDictDetailPage(QueryDictDetailArgs args) {
        Page<Object> page = new Page<>(args.getPage(), args.getSize());
        return new ResponseEntity<>(systemDictDetailService.describeDictDetailPage(args, page), HttpStatus.OK);
    }

    @ApiOperation("查询多个字典详情")
    @GetMapping(value = "/getDictDetailMaps")
    public ResponseEntity<Object> getDictDetailMaps(@RequestParam String dictName) {
        String[] names = dictName.split("[,，]");
        Map<String, List<DictDetailDO>> dictMap = new HashMap<>(16);
        for (String name : names) {
            dictMap.put(name, systemDictDetailService.describeDictDetailListByName(name));
        }
        return new ResponseEntity<>(dictMap, HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("新增字典详情")
    @PostMapping(value = "/saveDictDetail")
    @PreAuthorize("@el.check('dict:add')")
    public ResponseEntity<Object> saveDictDetail(@Validated @RequestBody CreateDictDetailArgs args) {
        systemDictDetailService.saveDictDetail(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @OperationLog
    @ApiOperation("修改字典详情")
    @PostMapping(value = "/modifyDictDetailById")
    @PreAuthorize("@el.check('dict:edit')")
    public ResponseEntity<Object> modifyDictDetailById(@Validated(DictDetailDO.Update.class) @RequestBody DictDetailDO resources) {
        systemDictDetailService.modifyDictDetailById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @OperationLog
    @ApiOperation("删除字典详情")
    @PostMapping(value = "/removeDictDetailById")
    @PreAuthorize("@el.check('dict:del')")
    public ResponseEntity<Object> removeDictDetailById(@RequestBody DictDetailDO args) {
        systemDictDetailService.removeDictDetailById(args.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
