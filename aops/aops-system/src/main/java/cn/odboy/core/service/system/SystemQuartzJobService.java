package cn.odboy.core.service.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.service.system.dto.QueryQuartzJobArgs;
import cn.odboy.core.service.system.dto.UpdateQuartzJobArgs;
import cn.odboy.core.dal.dataobject.job.QuartzJobDO;
import cn.odboy.core.dal.dataobject.job.QuartzLogDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemQuartzJobService extends IService<QuartzJobDO> {
    /**
     * 分页查询
     *
     * @param args 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<QuartzJobDO> describeQuartzJobPage(QueryQuartzJobArgs args, Page<Object> page);

    /**
     * 查询全部
     *
     * @param args 条件
     * @return /
     */
    List<QuartzJobDO> describeQuartzJobList(QueryQuartzJobArgs args);

    /**
     * 分页查询日志
     *
     * @param args 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<QuartzLogDO> describeQuartzLogPage(QueryQuartzJobArgs args, Page<Object> page);

    /**
     * 查询全部
     *
     * @param args 条件
     * @return /
     */
    List<QuartzLogDO> describeQuartzLogList(QueryQuartzJobArgs args);
    /**
     * 创建
     *
     * @param resources /
     */
    void createJob(QuartzJobDO resources);

    /**
     * 修改任务并重新调度
     *
     * @param args /
     */
    void modifyQuartzJobResumeCron(UpdateQuartzJobArgs args);

    /**
     * 删除任务
     *
     * @param ids /
     */
    void removeJobByIds(Set<Long> ids);

    /**
     * 更改定时任务状态
     *
     * @param quartzJobDO /
     */
    void switchQuartzJobStatus(QuartzJobDO quartzJobDO);

    /**
     * 立即执行定时任务
     *
     * @param quartzJobDO /
     */
    void startQuartzJob(QuartzJobDO quartzJobDO);

    /**
     * 导出定时任务
     *
     * @param queryAll 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadQuartzJobExcel(List<QuartzJobDO> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 导出定时任务日志
     *
     * @param queryAllLog 待导出的数据
     * @param response    /
     * @throws IOException /
     */
    void downloadQuartzLogExcel(List<QuartzLogDO> queryAllLog, HttpServletResponse response) throws IOException;

    /**
     * 执行子任务
     *
     * @param tasks /
     * @throws InterruptedException /
     */
    void startSubQuartJob(String[] tasks) throws InterruptedException;
}
