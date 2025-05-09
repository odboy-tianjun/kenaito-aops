package cn.odboy.core.service.tools;


import cn.odboy.core.service.tools.dto.SendEmailArgs;

public interface CaptchaService {
    /**
     * 发送验证码
     *
     * @param email /
     * @param key   /
     * @return /
     */
    SendEmailArgs renderCodeTemplate(String email, String key);


    /**
     * 验证
     *
     * @param code /
     * @param key  /
     */
    void checkCodeAvailable(String key, String email, String code);
}
