package cn.odboy.core.service.tools;

import cn.odboy.core.dal.dataobject.tools.QiniuConfig;
import com.baomidou.mybatisplus.extension.service.IService;


public interface QiniuConfigService extends IService<QiniuConfig> {

    /**
     * 保存
     *
     * @param type 类型
     */
    void saveQiniuConfig(QiniuConfig type);

    /**
     * 更新
     *
     * @param type 类型
     */
    void modifyQiniuConfigType(String type);
}
