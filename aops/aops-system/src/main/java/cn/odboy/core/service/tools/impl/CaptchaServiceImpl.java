package cn.odboy.core.service.tools.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.odboy.core.service.tools.dto.SendEmailRequest;
import cn.odboy.core.service.tools.CaptchaService;
import cn.odboy.exception.BadRequestException;
import cn.odboy.redis.RedisHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {
    private final RedisHelper redisHelper;
    @Value("${app.email.captchaSetting.expiration}")
    private Long expiration;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SendEmailRequest renderCodeTemplate(String email, String key) {
        SendEmailRequest sendEmailRequest;
        String content;
        String redisKey = key + email;
        // 如果不存在有效的验证码，就创建一个新的
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("email.ftl");
        String oldCode = redisHelper.get(redisKey, String.class);
        if (oldCode == null) {
            String code = RandomUtil.randomNumbers(6);
            // 存入缓存
            if (!redisHelper.set(redisKey, code, expiration)) {
                throw new BadRequestException("服务异常，请联系网站负责人");
            }
            // 存在就再次发送原来的验证码
            content = template.render(Dict.create().set("code", code));
        } else {
            content = template.render(Dict.create().set("code", oldCode));
        }
        sendEmailRequest = new SendEmailRequest(Collections.singletonList(email), "CuteJava后台管理系统", content);
        return sendEmailRequest;
    }

    @Override
    public void checkCodeAvailable(String key, String email, String code) {
        String value = redisHelper.get(key + email, String.class);
        if (value == null || !value.equals(code)) {
            throw new BadRequestException("无效验证码");
        } else {
            redisHelper.del(key);
        }
    }
}
