package cn.odboy.core.framework.quartz.core.handler;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.odboy.common.context.SpringBeanHolder;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.common.util.StringUtil;
import cn.odboy.core.dal.dataobject.job.QuartzJobDO;
import cn.odboy.core.dal.dataobject.job.QuartzLogDO;
import cn.odboy.core.dal.mysql.job.QuartzLogMapper;
import cn.odboy.core.service.system.SystemQuartzJobService;
import cn.odboy.core.service.tools.EmailService;
import cn.odboy.core.service.tools.dto.EmailSendArgs;
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
        QuartzJobDO quartzJobDO = (QuartzJobDO) context.getMergedJobDataMap().get(QuartzJobDO.JOB_KEY);
        // 获取spring bean
        QuartzLogMapper quartzLogMapper = SpringBeanHolder.getBean(QuartzLogMapper.class);
        SystemQuartzJobService systemQuartzJobService = SpringBeanHolder.getBean(SystemQuartzJobService.class);
        RedisHelper redisHelper = SpringBeanHolder.getBean(RedisHelper.class);

        String uuid = quartzJobDO.getUuid();

        QuartzLogDO quartzLogDO = new QuartzLogDO();
        quartzLogDO.setJobName(quartzJobDO.getJobName());
        quartzLogDO.setBeanName(quartzJobDO.getBeanName());
        quartzLogDO.setMethodName(quartzJobDO.getMethodName());
        quartzLogDO.setParams(quartzJobDO.getParams());
        long startTime = System.currentTimeMillis();
        quartzLogDO.setCronExpression(quartzJobDO.getCronExpression());
        try {
            // 执行任务
            QuartzRunnable task = new QuartzRunnable(quartzJobDO.getBeanName(), quartzJobDO.getMethodName(), quartzJobDO.getParams());
            Future<?> future = executor.submit(task);
            // 忽略任务执行结果
            future.get();
            long times = System.currentTimeMillis() - startTime;
            quartzLogDO.setTime(times);
            if (StringUtil.isNotBlank(uuid)) {
                redisHelper.set(uuid, true);
            }
            // 任务状态
            quartzLogDO.setIsSuccess(true);
            log.info("任务执行成功，任务名称：{}, 执行时间：{}毫秒", quartzJobDO.getJobName(), times);
            // 判断是否存在子任务
            if (StringUtil.isNotBlank(quartzJobDO.getSubTask())) {
                String[] tasks = quartzJobDO.getSubTask().split("[,，]");
                // 执行子任务
                systemQuartzJobService.startSubQuartJob(tasks);
            }
        } catch (Exception e) {
            if (StringUtil.isNotBlank(uuid)) {
                redisHelper.set(uuid, false);
            }
            log.error("任务执行失败，任务名称：{}", quartzJobDO.getJobName(), e);
            long times = System.currentTimeMillis() - startTime;
            quartzLogDO.setTime(times);
            // 任务状态 0：成功 1：失败
            quartzLogDO.setIsSuccess(false);
            quartzLogDO.setExceptionDetail(ExceptionUtil.stacktraceToString(e));
            // 任务如果失败了则暂停
            if (quartzJobDO.getPauseAfterFailure() != null && quartzJobDO.getPauseAfterFailure()) {
                quartzJobDO.setIsPause(false);
                // 更新状态
                systemQuartzJobService.switchQuartzJobStatus(quartzJobDO);
            }
            if (quartzJobDO.getEmail() != null) {
                EmailService emailService = SpringBeanHolder.getBean(EmailService.class);
                // 邮箱报警
                if (StringUtil.isNoneBlank(quartzJobDO.getEmail())) {
                    EmailSendArgs emailSendArgs = taskAlarm(quartzJobDO, ExceptionUtil.stacktraceToString(e));
                    emailService.sendEmail(emailSendArgs);
                }
            }
        } finally {
            quartzLogMapper.insert(quartzLogDO);
        }
    }

    private EmailSendArgs taskAlarm(QuartzJobDO quartzJobDO, String msg) {
        EmailSendArgs emailSendArgs = new EmailSendArgs();
        emailSendArgs.setSubject("定时任务【" + quartzJobDO.getJobName() + "】执行失败，请尽快处理！");
        Map<String, Object> data = new HashMap<>(16);
        data.put("task", quartzJobDO);
        data.put("msg", msg);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("taskAlarm.ftl");
        emailSendArgs.setContent(template.render(data));
        List<String> emails = Arrays.asList(quartzJobDO.getEmail().split("[,，]"));
        emailSendArgs.setTos(emails);
        return emailSendArgs;
    }
}
