package cn.odboy.core.controller.tools;

import cn.odboy.base.PageResult;
import cn.odboy.constant.FileTypeEnum;
import cn.odboy.core.api.tools.LocalStorageApi;
import cn.odboy.core.service.tools.dto.QueryLocalStorageRequest;
import cn.odboy.core.dal.dataobject.tools.LocalStorage;
import cn.odboy.core.service.tools.LocalStorageService;
import cn.odboy.exception.BadRequestException;
import cn.odboy.util.FileUtil;
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
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Api(tags = "工具：本地存储管理")
@RequestMapping("/api/localStorage")
public class LocalStorageController {
    private final LocalStorageService localStorageService;
    private final LocalStorageApi localStorageApi;

    @ApiOperation("查询文件")
    @GetMapping
    @PreAuthorize("@el.check('storage:list')")
    public ResponseEntity<PageResult<LocalStorage>> queryFile(QueryLocalStorageRequest criteria) {
        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
        return new ResponseEntity<>(localStorageApi.describeLocalStoragePage(criteria, page), HttpStatus.OK);
    }

    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('storage:list')")
    public void exportFile(HttpServletResponse response, QueryLocalStorageRequest criteria) throws IOException {
        localStorageService.downloadExcel(localStorageApi.describeLocalStorageList(criteria), response);
    }

    @ApiOperation("上传文件")
    @PostMapping(value = "/uploadFile")
    @PreAuthorize("@el.check('storage:add')")
    public ResponseEntity<Object> uploadFile(@RequestParam String name, @RequestParam("file") MultipartFile file) {
        localStorageService.uploadFile(name, file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("上传图片")
    @PostMapping("/uploadPicture")
    public ResponseEntity<LocalStorage> uploadPicture(@RequestParam MultipartFile file) {
        // 判断文件是否为图片
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
        if (!FileTypeEnum.IMAGE.getCode().equals(FileUtil.getFileType(suffix))) {
            throw new BadRequestException("只能上传图片");
        }
        LocalStorage localStorage = localStorageService.uploadFile(null, file);
        return new ResponseEntity<>(localStorage, HttpStatus.OK);
    }

    @ApiOperation("修改文件")
    @PostMapping(value = "/modifyLocalStorageById")
    @PreAuthorize("@el.check('storage:edit')")
    public ResponseEntity<Object> modifyLocalStorageById(@Validated @RequestBody LocalStorage resources) {
        localStorageService.modifyLocalStorageById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("多选删除")
    @PostMapping(value = "/removeFileByIds")
    public ResponseEntity<Object> deleteFileByIds(@RequestBody Long[] ids) {
        localStorageService.removeFileByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}