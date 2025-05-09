package cn.odboy.core.service.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.service.system.dto.QueryQuartzJobRequest;
import cn.odboy.core.service.system.dto.UpdateQuartzJobRequest;
import cn.odboy.core.dal.dataobject.job.QuartzJob;
import cn.odboy.core.dal.dataobject.job.QuartzLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemQuartzJobService extends IService<QuartzJob> {
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
    /**
     * 创建
     *
     * @param resources /
     */
    void createJob(QuartzJob resources);

    /**
     * 修改任务并重新调度
     *
     * @param resources /
     */
    void modifyQuartzJobResumeCron(UpdateQuartzJobRequest resources);

    /**
     * 删除任务
     *
     * @param ids /
     */
    void removeJobByIds(Set<Long> ids);

    /**
     * 更改定时任务状态
     *
     * @param quartzJob /
     */
    void switchQuartzJobStatus(QuartzJob quartzJob);

    /**
     * 立即执行定时任务
     *
     * @param quartzJob /
     */
    void startQuartzJob(QuartzJob quartzJob);

    /**
     * 导出定时任务
     *
     * @param queryAll 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadQuartzJobExcel(List<QuartzJob> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 导出定时任务日志
     *
     * @param queryAllLog 待导出的数据
     * @param response    /
     * @throws IOException /
     */
    void downloadQuartzLogExcel(List<QuartzLog> queryAllLog, HttpServletResponse response) throws IOException;

    /**
     * 执行子任务
     *
     * @param tasks /
     * @throws InterruptedException /
     */
    void startSubQuartJob(String[] tasks) throws InterruptedException;
}
