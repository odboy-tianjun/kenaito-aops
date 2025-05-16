package cn.odboy.core.service.tools;

import cn.odboy.core.dal.dataobject.tools.EmailConfigDO;
import cn.odboy.core.service.tools.dto.EmailSendArgs;
import com.baomidou.mybatisplus.extension.service.IService;


public interface EmailService extends IService<EmailConfigDO> {
    /**
     * 查询配置
     *
     * @return EmailConfigDO 邮件配置
     */
    EmailConfigDO describeEmailConfig();

    /**
     * 更新邮件配置
     *
     * @param emailConfigDO 邮箱配置
     * @return /
     * @throws Exception /
     */
    void modifyEmailConfigOnPassChange(EmailConfigDO emailConfigDO) throws Exception;

    /**
     * 发送邮件
     *
     * @param args 邮件发送的内容
     */
    void sendEmail(EmailSendArgs args);
}
