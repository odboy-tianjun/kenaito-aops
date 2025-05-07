package cn.odboy.core.api.system;

import cn.odboy.base.PageResult;
import cn.odboy.core.dal.dataobject.job.QuartzJob;
import cn.odboy.core.dal.dataobject.job.QuartzLog;
import cn.odboy.core.service.system.dto.QueryQuartzJobRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface SystemQuartzJobApi {
    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<QuartzJob> describeQuartzJobPage(QueryQuartzJobRequest criteria, Page<Object> page);

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @return /
     */
    List<QuartzJob> describeQuartzJobList(QueryQuartzJobRequest criteria);

    /**
     * 分页查询日志
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<QuartzLog> describeQuartzLogPage(QueryQuartzJobRequest criteria, Page<Object> page);

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @return /
     */
    List<QuartzLog> describeQuartzLogList(QueryQuartzJobRequest criteria);
}
