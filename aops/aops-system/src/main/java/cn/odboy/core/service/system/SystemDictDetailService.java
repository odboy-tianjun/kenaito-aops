package cn.odboy.core.service.system;

import cn.odboy.core.dal.dataobject.system.DictDetail;
import cn.odboy.core.service.system.dto.CreateDictDetailRequest;
import com.baomidou.mybatisplus.extension.service.IService;


public interface SystemDictDetailService extends IService<DictDetail> {

    /**
     * 创建
     *
     * @param resources /
     */
    void saveDictDetail(CreateDictDetailRequest resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyDictDetailById(DictDetail resources);

    /**
     * 删除
     *
     * @param id /
     */
    void removeDictDetailById(Long id);
}