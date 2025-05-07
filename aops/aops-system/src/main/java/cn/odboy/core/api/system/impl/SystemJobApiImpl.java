package cn.odboy.core.api.system.impl;

import cn.odboy.base.PageResult;
import cn.odboy.core.api.system.SystemJobApi;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.dal.dataobject.system.Job;
import cn.odboy.core.dal.mysql.system.JobMapper;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.core.service.system.dto.QueryJobRequest;
import cn.odboy.exception.BadRequestException;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SystemJobApiImpl implements SystemJobApi {
    private final JobMapper jobMapper;
    private final UserMapper userMapper;
    private final RedisHelper redisHelper;
    @Override
    public PageResult<Job> describeJobPage(QueryJobRequest criteria, Page<Object> page) {
        return PageUtil.toPage(jobMapper.queryJobPageByArgs(criteria, page));
    }

    @Override
    public List<Job> describeJobList(QueryJobRequest criteria) {
        return jobMapper.queryJobPageByArgs(criteria, PageUtil.getCount(jobMapper)).getRecords();
    }

    @Override
    public Job describeJobById(Long id) {
        String key = SystemRedisKey.JOB_ID + id;
        Job job = redisHelper.get(key, Job.class);
        if (job == null) {
            job = jobMapper.selectById(id);
            redisHelper.set(key, job, 1, TimeUnit.DAYS);
        }
        return job;
    }


    @Override
    public void verifyBindRelationByIds(Set<Long> ids) {
        if (userMapper.getUserCountByJobIds(ids) > 0) {
            throw new BadRequestException("所选的岗位中存在用户关联，请解除关联再试！");
        }
    }
}
