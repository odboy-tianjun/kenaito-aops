package cn.odboy.core.controller.tools;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.dal.dataobject.tools.QiniuConfigDO;
import cn.odboy.core.dal.dataobject.tools.QiniuContentDO;
import cn.odboy.core.service.tools.QiniuConfigService;
import cn.odboy.core.service.tools.QiniuContentService;
import cn.odboy.core.service.tools.dto.QiniuQueryArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 发送邮件
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qiNiuContent")
@Api(tags = "工具：七牛云存储管理")
public class QiniuController {
    private final QiniuContentService qiniuContentService;
    private final QiniuConfigService qiNiuConfigService;

    @ApiOperation("查询七牛云存储配置")
    @PostMapping(value = "/describeQiniuConfig")
    public ResponseEntity<QiniuConfigDO> describeQiniuConfig() {
        return new ResponseEntity<>(qiNiuConfigService.describeQiniuConfig(), HttpStatus.OK);
    }

    @ApiOperation("配置七牛云存储")
    @PostMapping(value = "/modifyQiniuConfig")
    public ResponseEntity<Object> modifyQiniuConfig(@Validated @RequestBody QiniuConfigDO qiniuConfigDO) {
        qiNiuConfigService.saveQiniuConfig(qiniuConfigDO);
        qiNiuConfigService.modifyQiniuConfigType(qiniuConfigDO.getType());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void exportQiNiu(HttpServletResponse response, QiniuQueryArgs args) throws IOException {
        qiniuContentService.downloadExcel(qiniuContentService.describeQiniuContentList(args), response);
    }

    @ApiOperation("查询文件")
    @GetMapping
    public ResponseEntity<PageResult<QiniuContentDO>> queryQiNiu(QiniuQueryArgs args) {
        Page<Object> page = new Page<>(args.getPage(), args.getSize());
        return new ResponseEntity<>(qiniuContentService.describeQiniuContentPage(args, page), HttpStatus.OK);
    }

    @ApiOperation("上传文件")
    @PostMapping
    public ResponseEntity<Object> uploadQiNiu(@RequestParam MultipartFile file) {
        QiniuContentDO qiniuContentDO = qiniuContentService.uploadFile(file);
        Map<String, Object> map = new HashMap<>(3);
        map.put("id", qiniuContentDO.getId());
        map.put("errno", 0);
        map.put("data", new String[]{qiniuContentDO.getUrl()});
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @ApiOperation("同步七牛云数据")
    @PostMapping(value = "/synchronize")
    public ResponseEntity<Object> synchronizeQiNiu() {
        qiniuContentService.synchronize();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("下载文件")
    @GetMapping(value = "/createFilePreviewUrl/{id}")
    public ResponseEntity<Object> createFilePreviewUrl(@PathVariable Long id) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("url", qiniuContentService.createFilePreviewUrl(qiniuContentService.getById(id)));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @ApiOperation("删除文件")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteQiNiu(@PathVariable Long id) {
        qiniuContentService.removeFileById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("删除多张图片")
    @DeleteMapping
    public ResponseEntity<Object> deleteAllQiNiu(@RequestBody Long[] ids) {
        qiniuContentService.removeFileByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
