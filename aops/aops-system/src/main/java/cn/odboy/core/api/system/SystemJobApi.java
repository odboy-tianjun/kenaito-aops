package cn.odboy.core.api.system;

import cn.odboy.base.PageResult;
import cn.odboy.core.dal.dataobject.system.Job;
import cn.odboy.core.service.system.dto.QueryJobRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Set;

public interface SystemJobApi {

    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<Job> describeJobPage(QueryJobRequest criteria, Page<Object> page);

    /**
     * 查询全部数据
     *
     * @param criteria /
     * @return /
     */
    List<Job> describeJobList(QueryJobRequest criteria);


    /**
     * 验证是否被用户关联
     *
     * @param ids /
     */
    void verifyBindRelationByIds(Set<Long> ids);

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    Job describeJobById(Long id);
}
