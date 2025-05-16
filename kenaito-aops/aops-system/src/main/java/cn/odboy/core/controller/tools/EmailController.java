package cn.odboy.core.controller.tools;

import cn.odboy.core.dal.dataobject.tools.EmailConfigDO;
import cn.odboy.core.service.tools.EmailService;
import cn.odboy.core.service.tools.dto.EmailSendArgs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发送邮件
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/email")
@Api(tags = "工具：邮件管理")
public class EmailController {
    private final EmailService emailService;

    @ApiOperation("查询配置")
    @PostMapping(value = "/describeEmailConfig")
    public ResponseEntity<EmailConfigDO> describeEmailConfig() {
        return new ResponseEntity<>(emailService.describeEmailConfig(), HttpStatus.OK);
    }

    @ApiOperation("配置邮件")
    @PostMapping(value = "/modifyEmailConfig")
    public ResponseEntity<Object> modifyEmailConfig(@Validated @RequestBody EmailConfigDO emailConfigDO) throws Exception {
        emailService.modifyEmailConfigOnPassChange(emailConfigDO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("发送邮件")
    @PostMapping(value = "/sendEmail")
    public ResponseEntity<Object> sendEmail(@Validated @RequestBody EmailSendArgs args) {
        emailService.sendEmail(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
