package cn.odboy.core.api.tools;

import cn.odboy.core.dal.dataobject.tools.EmailConfig;

public interface EmailApi {

    /**
     * 查询配置
     *
     * @return EmailConfig 邮件配置
     */
    EmailConfig describeEmailConfig();
}
