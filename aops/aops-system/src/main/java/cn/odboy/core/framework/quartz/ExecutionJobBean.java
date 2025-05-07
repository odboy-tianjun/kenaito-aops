package cn.odboy.core.framework.quartz;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.odboy.context.SpringBeanHolder;
import cn.odboy.core.service.tools.dto.SendEmailRequest;
import cn.odboy.core.dal.dataobject.job.QuartzJob;
import cn.odboy.core.dal.dataobject.job.QuartzLog;
import cn.odboy.core.dal.mysql.job.QuartzLogMapper;
import cn.odboy.core.service.system.SystemQuartzJobService;
import cn.odboy.core.service.tools.EmailService;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.QuartzJobBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Slf4j
public class ExecutionJobBean extends QuartzJobBean {
    /**
     * 此处仅供参考，可根据任务执行情况自定义线程池参数
     */
    private final ThreadPoolTaskExecutor executor = SpringBeanHolder.getBean("taskAsync");

    @Override
    public void executeInternal(JobExecutionContext context) {
        // 获取任务
        QuartzJob quartzJob = (QuartzJob) context.getMergedJobDataMap().get(QuartzJob.JOB_KEY);
        // 获取spring bean
        QuartzLogMapper quartzLogMapper = SpringBeanHolder.getBean(QuartzLogMapper.class);
        SystemQuartzJobService systemQuartzJobService = SpringBeanHolder.getBean(SystemQuartzJobService.class);
        RedisHelper redisHelper = SpringBeanHolder.getBean(RedisHelper.class);

        String uuid = quartzJob.getUuid();

        QuartzLog quartzLog = new QuartzLog();
        quartzLog.setJobName(quartzJob.getJobName());
        quartzLog.setBeanName(quartzJob.getBeanName());
        quartzLog.setMethodName(quartzJob.getMethodName());
        quartzLog.setParams(quartzJob.getParams());
        long startTime = System.currentTimeMillis();
        quartzLog.setCronExpression(quartzJob.getCronExpression());
        try {
            // 执行任务
            QuartzRunnable task = new QuartzRunnable(quartzJob.getBeanName(), quartzJob.getMethodName(), quartzJob.getParams());
            Future<?> future = executor.submit(task);
            // 忽略任务执行结果
            future.get();
            long times = System.currentTimeMillis() - startTime;
            quartzLog.setTime(times);
            if (StringUtil.isNotBlank(uuid)) {
                redisHelper.set(uuid, true);
            }
            // 任务状态
            quartzLog.setIsSuccess(true);
            log.info("任务执行成功，任务名称：{}, 执行时间：{}毫秒", quartzJob.getJobName(), times);
            // 判断是否存在子任务
            if (StringUtil.isNotBlank(quartzJob.getSubTask())) {
                String[] tasks = quartzJob.getSubTask().split("[,，]");
                // 执行子任务
                systemQuartzJobService.startSubQuartJob(tasks);
            }
        } catch (Exception e) {
            if (StringUtil.isNotBlank(uuid)) {
                redisHelper.set(uuid, false);
            }
            log.error("任务执行失败，任务名称：{}", quartzJob.getJobName(), e);
            long times = System.currentTimeMillis() - startTime;
            quartzLog.setTime(times);
            // 任务状态 0：成功 1：失败
            quartzLog.setIsSuccess(false);
            quartzLog.setExceptionDetail(ExceptionUtil.stacktraceToString(e));
            // 任务如果失败了则暂停
            if (quartzJob.getPauseAfterFailure() != null && quartzJob.getPauseAfterFailure()) {
                quartzJob.setIsPause(false);
                // 更新状态
                systemQuartzJobService.switchQuartzJobStatus(quartzJob);
            }
            if (quartzJob.getEmail() != null) {
                EmailService emailService = SpringBeanHolder.getBean(EmailService.class);
                // 邮箱报警
                if (StringUtil.isNoneBlank(quartzJob.getEmail())) {
                    SendEmailRequest sendEmailRequest = taskAlarm(quartzJob, ExceptionUtil.stacktraceToString(e));
                    emailService.sendEmail(sendEmailRequest);
                }
            }
        } finally {
            quartzLogMapper.insert(quartzLog);
        }
    }

    private SendEmailRequest taskAlarm(QuartzJob quartzJob, String msg) {
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setSubject("定时任务【" + quartzJob.getJobName() + "】执行失败，请尽快处理！");
        Map<String, Object> data = new HashMap<>(16);
        data.put("task", quartzJob);
        data.put("msg", msg);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("taskAlarm.ftl");
        sendEmailRequest.setContent(template.render(data));
        List<String> emails = Arrays.asList(quartzJob.getEmail().split("[,，]"));
        sendEmailRequest.setTos(emails);
        return sendEmailRequest;
    }
}
