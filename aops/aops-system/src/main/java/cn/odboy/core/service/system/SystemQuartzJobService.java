package cn.odboy.core.service.system;

import cn.odboy.core.dal.dataobject.job.QuartzJob;
import cn.odboy.core.dal.dataobject.job.QuartzLog;
import cn.odboy.core.service.system.dto.UpdateQuartzJobRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemQuartzJobService extends IService<QuartzJob> {
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
