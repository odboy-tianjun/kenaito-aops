package cn.odboy.core.service.system.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.core.dal.dataobject.job.QuartzJob;
import cn.odboy.core.dal.dataobject.job.QuartzLog;
import cn.odboy.core.dal.mysql.job.QuartzJobMapper;
import cn.odboy.core.dal.mysql.job.QuartzLogMapper;
import cn.odboy.core.framework.quartz.context.QuartzManage;
import cn.odboy.core.service.system.SystemQuartzJobService;
import cn.odboy.core.service.system.dto.UpdateQuartzJobRequest;
import cn.odboy.exception.BadRequestException;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.FileUtil;
import cn.odboy.util.StringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service(value = "quartzJobService")
public class SystemQuartzJobServiceImpl extends ServiceImpl<QuartzJobMapper, QuartzJob> implements SystemQuartzJobService {

    private final QuartzJobMapper quartzJobMapper;
    private final QuartzLogMapper quartzLogMapper;
    private final QuartzManage quartzManage;
    private final RedisHelper redisHelper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createJob(QuartzJob resources) {
        if (!CronExpression.isValidExpression(resources.getCronExpression())) {
            throw new BadRequestException("cron表达式格式错误");
        }
        save(resources);
        quartzManage.addJob(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyQuartzJobResumeCron(UpdateQuartzJobRequest resources) {
        if (!CronExpression.isValidExpression(resources.getCronExpression())) {
            throw new BadRequestException("cron表达式格式错误");
        }
        if (StringUtil.isNotBlank(resources.getSubTask())) {
            List<String> tasks = Arrays.asList(resources.getSubTask().split("[,，]"));
            if (tasks.contains(resources.getId().toString())) {
                throw new BadRequestException("子任务中不能添加当前任务ID");
            }
        }
        QuartzJob quartzJob = BeanUtil.copyProperties(resources, QuartzJob.class);
        saveOrUpdate(quartzJob);
        quartzManage.updateJobCron(quartzJob);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchQuartzJobStatus(QuartzJob quartzJob) {
        // 置换暂停状态
        if (quartzJob.getIsPause()) {
            quartzManage.resumeJob(quartzJob);
            quartzJob.setIsPause(false);
        } else {
            quartzManage.pauseJob(quartzJob);
            quartzJob.setIsPause(true);
        }
        saveOrUpdate(quartzJob);
    }

    @Override
    public void startQuartzJob(QuartzJob quartzJob) {
        quartzManage.runJobNow(quartzJob);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeJobByIds(Set<Long> ids) {
        for (Long id : ids) {
            QuartzJob quartzJob = getById(id);
            quartzManage.deleteJob(quartzJob);
            removeById(quartzJob);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startSubQuartJob(String[] tasks) throws InterruptedException {
        for (String id : tasks) {
            if (StrUtil.isBlank(id)) {
                // 如果是手动清除子任务id，会出现id为空字符串的问题
                continue;
            }
            QuartzJob quartzJob = getById(Long.parseLong(id));
            if (quartzJob == null) {
                // 防止子任务不存在
                continue;
            }
            // 执行任务
            String uuid = IdUtil.simpleUUID();
            quartzJob.setUuid(uuid);
            // 执行任务
            startQuartzJob(quartzJob);
            // 获取执行状态，如果执行失败则停止后面的子任务执行
            Boolean result = redisHelper.get(uuid, Boolean.class);
            while (result == null) {
                // 休眠5秒，再次获取子任务执行情况
                Thread.sleep(5000);
                result = redisHelper.get(uuid, Boolean.class);
            }
            if (!result) {
                redisHelper.del(uuid);
                break;
            }
        }
    }

    @Override
    public void downloadQuartzJobExcel(List<QuartzJob> quartzJobs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuartzJob quartzJob : quartzJobs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("任务名称", quartzJob.getJobName());
            map.put("Bean名称", quartzJob.getBeanName());
            map.put("执行方法", quartzJob.getMethodName());
            map.put("参数", quartzJob.getParams());
            map.put("表达式", quartzJob.getCronExpression());
            map.put("状态", quartzJob.getIsPause() ? "暂停中" : "运行中");
            map.put("描述", quartzJob.getDescription());
            map.put("创建日期", quartzJob.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadQuartzLogExcel(List<QuartzLog> queryAllLog, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuartzLog quartzLog : queryAllLog) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("任务名称", quartzLog.getJobName());
            map.put("Bean名称", quartzLog.getBeanName());
            map.put("执行方法", quartzLog.getMethodName());
            map.put("参数", quartzLog.getParams());
            map.put("表达式", quartzLog.getCronExpression());
            map.put("异常详情", quartzLog.getExceptionDetail());
            map.put("耗时/毫秒", quartzLog.getTime());
            map.put("状态", quartzLog.getIsSuccess() ? "成功" : "失败");
            map.put("创建日期", quartzLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
