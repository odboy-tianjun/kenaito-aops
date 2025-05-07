package cn.odboy.core.api.tools;

import cn.odboy.core.dal.dataobject.tools.QiniuConfig;

public interface QiniuConfigApi {

    /**
     * 查询配置
     *
     * @return QiniuConfig
     */
    QiniuConfig describeQiniuConfig();
}
