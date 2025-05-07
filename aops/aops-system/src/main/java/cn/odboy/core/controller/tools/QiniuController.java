package cn.odboy.core.controller.tools;

import cn.odboy.base.PageResult;
import cn.odboy.core.api.tools.QiniuConfigApi;
import cn.odboy.core.api.tools.QiniuContentApi;
import cn.odboy.core.service.tools.dto.QueryQiniuRequest;
import cn.odboy.core.dal.dataobject.tools.QiniuConfig;
import cn.odboy.core.dal.dataobject.tools.QiniuContent;
import cn.odboy.core.service.tools.QiniuConfigService;
import cn.odboy.core.service.tools.QiniuContentService;
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
    private final QiniuConfigApi qiniuConfigApi;
    private final QiniuContentApi qiniuContentApi;

    @ApiOperation("查询七牛云存储配置")
    @PostMapping(value = "/describeQiniuConfig")
    public ResponseEntity<QiniuConfig> describeQiniuConfig() {
        return new ResponseEntity<>(qiniuConfigApi.describeQiniuConfig(), HttpStatus.OK);
    }

    @ApiOperation("配置七牛云存储")
    @PostMapping(value = "/modifyQiniuConfig")
    public ResponseEntity<Object> modifyQiniuConfig(@Validated @RequestBody QiniuConfig qiniuConfig) {
        qiNiuConfigService.saveQiniuConfig(qiniuConfig);
        qiNiuConfigService.modifyQiniuConfigType(qiniuConfig.getType());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void exportQiNiu(HttpServletResponse response, QueryQiniuRequest criteria) throws IOException {
        qiniuContentService.downloadExcel(qiniuContentApi.describeQiniuContentList(criteria), response);
    }

    @ApiOperation("查询文件")
    @GetMapping
    public ResponseEntity<PageResult<QiniuContent>> queryQiNiu(QueryQiniuRequest criteria) {
        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
        return new ResponseEntity<>(qiniuContentApi.describeQiniuContentPage(criteria, page), HttpStatus.OK);
    }

    @ApiOperation("上传文件")
    @PostMapping
    public ResponseEntity<Object> uploadQiNiu(@RequestParam MultipartFile file) {
        QiniuContent qiniuContent = qiniuContentService.uploadFile(file);
        Map<String, Object> map = new HashMap<>(3);
        map.put("id", qiniuContent.getId());
        map.put("errno", 0);
        map.put("data", new String[]{qiniuContent.getUrl()});
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
