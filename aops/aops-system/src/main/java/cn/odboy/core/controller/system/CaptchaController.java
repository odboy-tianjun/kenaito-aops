package cn.odboy.core.controller.system;

import cn.odboy.core.constant.CaptchaBizEnum;
import cn.odboy.core.service.tools.CaptchaService;
import cn.odboy.core.service.tools.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code")
@Api(tags = "系统：验证码管理")
public class CaptchaController {
    private final CaptchaService captchaService;
    private final EmailService emailService;

    /**
     * 除了这个其他的接口都没有用上
     *
     * @param email 邮件地址
     */
    @ApiOperation("重置邮箱，发送验证码")
    @PostMapping(value = "/sendResetEmailCaptcha")
    public ResponseEntity<Object> sendResetEmailCaptcha(@RequestParam String email) {
        emailService.sendEmail(captchaService.renderCodeTemplate(email, CaptchaBizEnum.EMAIL_RESET_EMAIL_CODE.getRedisKey()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("重置密码，发送验证码")
    @PostMapping(value = "/sendResetPasswordCaptcha")
    public ResponseEntity<Object> sendResetPasswordCaptcha(@RequestParam String email) {
        emailService.sendEmail(captchaService.renderCodeTemplate(email, CaptchaBizEnum.EMAIL_RESET_PWD_CODE.getRedisKey()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("验证码验证")
    @PostMapping(value = "/checkCodeAvailable")
    public ResponseEntity<Object> checkCodeAvailable(@RequestParam String email, @RequestParam String code, @RequestParam String codeBi) {
        CaptchaBizEnum biEnum = CaptchaBizEnum.getByBizCode(codeBi);
        switch (Objects.requireNonNull(biEnum)) {
            case EMAIL_RESET_EMAIL_CODE:
                captchaService.checkCodeAvailable(CaptchaBizEnum.EMAIL_RESET_EMAIL_CODE.getBizCode(), email, code);
                break;
            case EMAIL_RESET_PWD_CODE:
                captchaService.checkCodeAvailable(CaptchaBizEnum.EMAIL_RESET_PWD_CODE.getBizCode(), email, code);
                break;
            default:
                break;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
