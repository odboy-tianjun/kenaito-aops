package cn.odboy.core.service.tools;

import cn.odboy.core.dal.dataobject.tools.QiniuConfigDO;
import com.baomidou.mybatisplus.extension.service.IService;


public interface QiniuConfigService extends IService<QiniuConfigDO> {

    /**
     * 查询配置
     *
     * @return QiniuConfigDO
     */
    QiniuConfigDO describeQiniuConfig();

    /**
     * 保存
     *
     * @param type 类型
     */
    void saveQiniuConfig(QiniuConfigDO type);

    /**
     * 更新
     *
     * @param type 类型
     */
    void modifyQiniuConfigType(String type);
}
