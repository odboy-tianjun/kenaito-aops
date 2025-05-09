package cn.odboy.core.service.tools;

import cn.odboy.core.service.tools.dto.SendEmailRequest;
import cn.odboy.core.dal.dataobject.tools.EmailConfig;
import com.baomidou.mybatisplus.extension.service.IService;


public interface EmailService extends IService<EmailConfig> {
    /**
     * 查询配置
     *
     * @return EmailConfig 邮件配置
     */
    EmailConfig describeEmailConfig();
    /**
     * 更新邮件配置
     *
     * @param emailConfig 邮箱配置
     * @return /
     * @throws Exception /
     */
    void modifyEmailConfigOnPassChange(EmailConfig emailConfig) throws Exception;

    /**
     * 发送邮件
     *
     * @param sendEmailRequest 邮件发送的内容
     */
    void sendEmail(SendEmailRequest sendEmailRequest);
}
