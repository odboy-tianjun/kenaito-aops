package cn.odboy.core.service.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.service.system.dto.CreateDictDetailArgs;
import cn.odboy.core.dal.dataobject.system.DictDetailDO;
import cn.odboy.core.service.system.dto.QueryDictDetailArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;


public interface SystemDictDetailService extends IService<DictDetailDO> {
    /**
     * 分页查询
     *
     * @param args 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<DictDetailDO> describeDictDetailPage(QueryDictDetailArgs args, Page<Object> page);

    /**
     * 根据字典名称获取字典详情
     *
     * @param name 字典名称
     * @return /
     */
    List<DictDetailDO> describeDictDetailListByName(String name);
    /**
     * 创建
     *
     * @param args /
     */
    void saveDictDetail(CreateDictDetailArgs args);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyDictDetailById(DictDetailDO resources);

    /**
     * 删除
     *
     * @param id /
     */
    void removeDictDetailById(Long id);
}