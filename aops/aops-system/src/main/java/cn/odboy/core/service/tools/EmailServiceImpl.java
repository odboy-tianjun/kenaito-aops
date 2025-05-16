package cn.odboy.core.service.tools;

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.util.DesEncryptUtil;
import cn.odboy.core.dal.dataobject.tools.EmailConfigDO;
import cn.odboy.core.dal.mysql.tools.EmailConfigMapper;
import cn.odboy.core.service.tools.dto.EmailSendArgs;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CacheConfig(cacheNames = "email")
public class EmailServiceImpl extends ServiceImpl<EmailConfigMapper, EmailConfigDO> implements EmailService {
    @Lazy
    @Autowired
    private EmailService emailService;

    @Override
    @Cacheable(key = "'config'")
    public EmailConfigDO describeEmailConfig() {
        EmailConfigDO emailConfigDO = getById(1L);
        return emailConfigDO == null ? new EmailConfigDO() : emailConfigDO;
    }

    @Override
    @CachePut(key = "'config'")
    @Transactional(rollbackFor = Exception.class)
    public void modifyEmailConfigOnPassChange(EmailConfigDO emailConfigDO) throws Exception {
        EmailConfigDO localConfig = emailService.describeEmailConfig();
        if (!emailConfigDO.getPassword().equals(localConfig.getPassword())) {
            // 对称加密
            emailConfigDO.setPassword(DesEncryptUtil.desEncrypt(emailConfigDO.getPassword()));
        }
        emailConfigDO.setId(1L);
        saveOrUpdate(emailConfigDO);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEmail(EmailSendArgs args) {
        EmailConfigDO emailConfigDO = emailService.describeEmailConfig();
        if (emailConfigDO.getId() == null) {
            throw new BadRequestException("请先配置，再操作");
        }
        // 封装
        MailAccount account = new MailAccount();
        // 设置用户
        String user = emailConfigDO.getFromUser().split("@")[0];
        account.setUser(user);
        account.setHost(emailConfigDO.getHost());
        account.setPort(Integer.parseInt(emailConfigDO.getPort()));
        account.setAuth(true);
        try {
            // 对称解密
            account.setPass(DesEncryptUtil.desDecrypt(emailConfigDO.getPassword()));
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        account.setFrom(emailConfigDO.getUser() + "<" + emailConfigDO.getFromUser() + ">");
        // ssl方式发送
        account.setSslEnable(true);
        // 使用STARTTLS安全连接
        account.setStarttlsEnable(true);
        // 解决jdk8之后默认禁用部分tls协议，导致邮件发送失败的问题
        account.setSslProtocols("TLSv1 TLSv1.1 TLSv1.2");
        String content = args.getContent();
        // 发送
        try {
            int size = args.getTos().size();
            Mail.create(account)
                    .setTos(args.getTos().toArray(new String[size]))
                    .setTitle(args.getSubject())
                    .setContent(content)
                    .setHtml(true)
                    // 关闭session
                    .setUseGlobalSession(false)
                    .send();
        } catch (Exception e) {
            log.error("邮件发送失败", e);
            throw new BadRequestException("邮件发送失败");
        }
    }
}
