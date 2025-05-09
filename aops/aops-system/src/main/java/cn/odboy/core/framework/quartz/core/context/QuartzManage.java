package cn.odboy.core.framework.quartz.core.context;

import cn.odboy.core.framework.quartz.core.handler.ExecutionJobBean;
import cn.odboy.core.dal.dataobject.job.QuartzJobDO;
import cn.odboy.common.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Component
public class QuartzManage {

    private static final String JOB_NAME = "TASK_";

    @Resource
    private Scheduler scheduler;

    public void addJob(QuartzJobDO quartzJobDO) {
        try {
            // 构建job信息
            JobDetail jobDetail = JobBuilder.newJob(ExecutionJobBean.class).
                    withIdentity(JOB_NAME + quartzJobDO.getId()).build();

            // 通过触发器名和cron 表达式创建 Trigger
            Trigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(JOB_NAME + quartzJobDO.getId())
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(quartzJobDO.getCronExpression()))
                    .build();

            cronTrigger.getJobDataMap().put(QuartzJobDO.JOB_KEY, quartzJobDO);

            // 重置启动时间
            ((CronTriggerImpl) cronTrigger).setStartTime(new Date());

            // 执行定时任务，如果是持久化的，这里会报错，捕获输出
            try {
                scheduler.scheduleJob(jobDetail, cronTrigger);
            } catch (ObjectAlreadyExistsException e) {
                log.warn("定时任务已存在，跳过加载");
            }

            // 暂停任务
            if (quartzJobDO.getIsPause()) {
                pauseJob(quartzJobDO);
            }
        } catch (Exception e) {
            log.error("创建定时任务失败", e);
            throw new BadRequestException("创建定时任务失败");
        }
    }

    /**
     * 更新job cron表达式
     *
     * @param quartzJobDO /
     */
    public void updateJobCron(QuartzJobDO quartzJobDO) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJobDO.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // 如果不存在则创建一个定时任务
            if (trigger == null) {
                addJob(quartzJobDO);
                trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            }
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzJobDO.getCronExpression());
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            // 重置启动时间
            ((CronTriggerImpl) trigger).setStartTime(new Date());
            trigger.getJobDataMap().put(QuartzJobDO.JOB_KEY, quartzJobDO);

            scheduler.rescheduleJob(triggerKey, trigger);
            // 暂停任务
            if (quartzJobDO.getIsPause()) {
                pauseJob(quartzJobDO);
            }
        } catch (Exception e) {
            log.error("更新定时任务失败", e);
            throw new BadRequestException("更新定时任务失败");
        }

    }

    /**
     * 删除一个job
     *
     * @param quartzJobDO /
     */
    public void deleteJob(QuartzJobDO quartzJobDO) {
        try {
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJobDO.getId());
            scheduler.pauseJob(jobKey);
            scheduler.deleteJob(jobKey);
        } catch (Exception e) {
            log.error("删除定时任务失败", e);
            throw new BadRequestException("删除定时任务失败");
        }
    }

    /**
     * 恢复一个job
     *
     * @param quartzJobDO /
     */
    public void resumeJob(QuartzJobDO quartzJobDO) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJobDO.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // 如果不存在则创建一个定时任务
            if (trigger == null) {
                addJob(quartzJobDO);
            }
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJobDO.getId());
            scheduler.resumeJob(jobKey);
        } catch (Exception e) {
            log.error("恢复定时任务失败", e);
            throw new BadRequestException("恢复定时任务失败");
        }
    }

    /**
     * 立即执行job
     *
     * @param quartzJobDO /
     */
    public void runJobNow(QuartzJobDO quartzJobDO) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJobDO.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // 如果不存在则创建一个定时任务
            if (trigger == null) {
                addJob(quartzJobDO);
            }
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(QuartzJobDO.JOB_KEY, quartzJobDO);
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJobDO.getId());
            scheduler.triggerJob(jobKey, dataMap);
        } catch (Exception e) {
            log.error("定时任务执行失败", e);
            throw new BadRequestException("定时任务执行失败");
        }
    }

    /**
     * 暂停一个job
     *
     * @param quartzJobDO /
     */
    public void pauseJob(QuartzJobDO quartzJobDO) {
        try {
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJobDO.getId());
            scheduler.pauseJob(jobKey);
        } catch (Exception e) {
            log.error("定时任务暂停失败", e);
            throw new BadRequestException("定时任务暂停失败");
        }
    }
}
