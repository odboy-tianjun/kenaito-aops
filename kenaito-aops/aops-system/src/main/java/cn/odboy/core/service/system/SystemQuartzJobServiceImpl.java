package cn.odboy.core.service.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.model.PageResult;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.common.util.FileUtil;
import cn.odboy.common.util.PageUtil;
import cn.odboy.common.util.StringUtil;
import cn.odboy.core.dal.dataobject.job.QuartzJobDO;
import cn.odboy.core.dal.dataobject.job.QuartzLogDO;
import cn.odboy.core.dal.mysql.job.QuartzJobMapper;
import cn.odboy.core.dal.mysql.job.QuartzLogMapper;
import cn.odboy.core.framework.quartz.core.context.QuartzManage;
import cn.odboy.core.service.system.dto.QuartzJobQueryArgs;
import cn.odboy.core.service.system.dto.QuartzJobUpdateArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class SystemQuartzJobServiceImpl extends ServiceImpl<QuartzJobMapper, QuartzJobDO> implements SystemQuartzJobService {
    private final QuartzJobMapper quartzJobMapper;
    private final QuartzLogMapper quartzLogMapper;
    private final QuartzManage quartzManage;
    private final RedisHelper redisHelper;

    @Override
    public PageResult<QuartzJobDO> describeQuartzJobPage(QuartzJobQueryArgs args, Page<Object> page) {
        return PageUtil.toPage(quartzJobMapper.queryQuartzJobPageByArgs(args, page));
    }

    @Override
    public PageResult<QuartzLogDO> describeQuartzLogPage(QuartzJobQueryArgs args, Page<Object> page) {
        return PageUtil.toPage(quartzLogMapper.queryQuartzLogPageByArgs(args, page));
    }

    @Override
    public List<QuartzJobDO> describeQuartzJobList(QuartzJobQueryArgs args) {
        return quartzJobMapper.queryQuartzJobPageByArgs(args, PageUtil.getCount(quartzJobMapper)).getRecords();
    }

    @Override
    public List<QuartzLogDO> describeQuartzLogList(QuartzJobQueryArgs args) {
        return quartzLogMapper.queryQuartzLogPageByArgs(args, PageUtil.getCount(quartzLogMapper)).getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createJob(QuartzJobDO resources) {
        if (!CronExpression.isValidExpression(resources.getCronExpression())) {
            throw new BadRequestException("cron表达式格式错误");
        }
        save(resources);
        quartzManage.addJob(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyQuartzJobResumeCron(QuartzJobUpdateArgs args) {
        if (!CronExpression.isValidExpression(args.getCronExpression())) {
            throw new BadRequestException("cron表达式格式错误");
        }
        if (StringUtil.isNotBlank(args.getSubTask())) {
            List<String> tasks = Arrays.asList(args.getSubTask().split("[,，]"));
            if (tasks.contains(args.getId().toString())) {
                throw new BadRequestException("子任务中不能添加当前任务ID");
            }
        }
        QuartzJobDO quartzJobDO = BeanUtil.copyProperties(args, QuartzJobDO.class);
        saveOrUpdate(quartzJobDO);
        quartzManage.updateJobCron(quartzJobDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchQuartzJobStatus(QuartzJobDO quartzJobDO) {
        // 置换暂停状态
        if (quartzJobDO.getIsPause()) {
            quartzManage.resumeJob(quartzJobDO);
            quartzJobDO.setIsPause(false);
        } else {
            quartzManage.pauseJob(quartzJobDO);
            quartzJobDO.setIsPause(true);
        }
        saveOrUpdate(quartzJobDO);
    }

    @Override
    public void startQuartzJob(QuartzJobDO quartzJobDO) {
        quartzManage.runJobNow(quartzJobDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeJobByIds(Set<Long> ids) {
        for (Long id : ids) {
            QuartzJobDO quartzJobDO = getById(id);
            quartzManage.deleteJob(quartzJobDO);
            removeById(quartzJobDO);
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
            QuartzJobDO quartzJobDO = getById(Long.parseLong(id));
            if (quartzJobDO == null) {
                // 防止子任务不存在
                continue;
            }
            // 执行任务
            String uuid = IdUtil.simpleUUID();
            quartzJobDO.setUuid(uuid);
            // 执行任务
            startQuartzJob(quartzJobDO);
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
    public void downloadQuartzJobExcel(List<QuartzJobDO> quartzJobDOS, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuartzJobDO quartzJobDO : quartzJobDOS) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("任务名称", quartzJobDO.getJobName());
            map.put("Bean名称", quartzJobDO.getBeanName());
            map.put("执行方法", quartzJobDO.getMethodName());
            map.put("参数", quartzJobDO.getParams());
            map.put("表达式", quartzJobDO.getCronExpression());
            map.put("状态", quartzJobDO.getIsPause() ? "暂停中" : "运行中");
            map.put("描述", quartzJobDO.getDescription());
            map.put("创建日期", quartzJobDO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadQuartzLogExcel(List<QuartzLogDO> queryAllLog, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuartzLogDO quartzLogDO : queryAllLog) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("任务名称", quartzLogDO.getJobName());
            map.put("Bean名称", quartzLogDO.getBeanName());
            map.put("执行方法", quartzLogDO.getMethodName());
            map.put("参数", quartzLogDO.getParams());
            map.put("表达式", quartzLogDO.getCronExpression());
            map.put("异常详情", quartzLogDO.getExceptionDetail());
            map.put("耗时/毫秒", quartzLogDO.getTime());
            map.put("状态", quartzLogDO.getIsSuccess() ? "成功" : "失败");
            map.put("创建日期", quartzLogDO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
