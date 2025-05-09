package cn.odboy.core.service.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.service.system.dto.CreateDictDetailRequest;
import cn.odboy.core.dal.dataobject.system.DictDetail;
import cn.odboy.core.service.system.dto.QueryDictDetailRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;


public interface SystemDictDetailService extends IService<DictDetail> {
    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<DictDetail> describeDictDetailPage(QueryDictDetailRequest criteria, Page<Object> page);

    /**
     * 根据字典名称获取字典详情
     *
     * @param name 字典名称
     * @return /
     */
    List<DictDetail> describeDictDetailListByName(String name);
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