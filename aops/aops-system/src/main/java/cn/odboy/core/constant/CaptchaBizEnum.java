package cn.odboy.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码业务场景
 */
@Getter
@AllArgsConstructor
public enum CaptchaBizEnum {

    /* 通过手机号码重置邮箱 */
    PHONE_RESET_EMAIL_CODE("10000", "phone_reset_email_code_", "通过手机号码重置邮箱"),

    /* 通过旧邮箱重置邮箱 */
    EMAIL_RESET_EMAIL_CODE("10001", "email_reset_email_code_", "通过旧邮箱重置邮箱"),

    /* 通过手机号码重置密码 */
    PHONE_RESET_PWD_CODE("10002", "phone_reset_pwd_code_", "通过手机号码重置密码"),

    /* 通过邮箱重置密码 */
    EMAIL_RESET_PWD_CODE("10003", "email_reset_pwd_code_", "通过邮箱重置密码");

    /**
     * 业务编码
     */
    private final String bizCode;
    /**
     * redis中的key
     */
    private final String redisKey;
    /**
     * 业务描述
     */
    private final String description;

    public static CaptchaBizEnum getByBizCode(String bizCode) {
        for (CaptchaBizEnum value : CaptchaBizEnum.values()) {
            if (value.getBizCode().equals(bizCode)) {
                return value;
            }
        }
        return null;
    }
}
