package cn.odboy.core.api.tools.api;

import cn.odboy.core.dal.dataobject.tools.QiniuConfig;

public interface QiniuConfigApi {

    /**
     * 查询配置
     *
     * @return QiniuConfig
     */
    QiniuConfig describeQiniuConfig();
}
