package cn.odboy.core.service.system;

import cn.hutool.core.bean.BeanUtil;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.exception.EntityExistException;
import cn.odboy.common.model.PageResult;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.common.util.FileUtil;
import cn.odboy.common.util.PageUtil;
import cn.odboy.core.dal.dataobject.system.JobDO;
import cn.odboy.core.dal.mysql.system.JobMapper;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.core.dal.redis.RedisKeyConst;
import cn.odboy.core.service.system.dto.JobCreateArgs;
import cn.odboy.core.service.system.dto.JobQueryArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SystemJobServiceImpl extends ServiceImpl<JobMapper, JobDO> implements SystemJobService {
    private final JobMapper jobMapper;
    private final RedisHelper redisHelper;
    private final UserMapper userMapper;

    @Override
    public PageResult<JobDO> describeJobPage(JobQueryArgs args, Page<Object> page) {
        return PageUtil.toPage(jobMapper.queryJobPageByArgs(args, page));
    }

    @Override
    public List<JobDO> describeJobList(JobQueryArgs args) {
        return jobMapper.queryJobPageByArgs(args, PageUtil.getCount(jobMapper)).getRecords();
    }

    @Override
    public JobDO describeJobById(Long id) {
        String key = RedisKeyConst.JOB_ID + id;
        JobDO jobDO = redisHelper.get(key, JobDO.class);
        if (jobDO == null) {
            jobDO = jobMapper.selectById(id);
            redisHelper.set(key, jobDO, 1, TimeUnit.DAYS);
        }
        return jobDO;
    }


    @Override
    public void verifyBindRelationByIds(Set<Long> ids) {
        if (userMapper.getUserCountByJobIds(ids) > 0) {
            throw new BadRequestException("所选的岗位中存在用户关联，请解除关联再试！");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveJob(JobCreateArgs args) {
        JobDO jobDO = jobMapper.getJobByName(args.getName());
        if (jobDO != null) {
            throw new EntityExistException(JobDO.class, "name", args.getName());
        }
        save(BeanUtil.copyProperties(args, JobDO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyJobById(JobDO resources) {
        JobDO jobDO = getById(resources.getId());
        JobDO old = jobMapper.getJobByName(resources.getName());
        if (old != null && !old.getId().equals(resources.getId())) {
            throw new EntityExistException(JobDO.class, "name", resources.getName());
        }
        resources.setId(jobDO.getId());
        saveOrUpdate(resources);
        // 删除缓存
        delCaches(resources.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeJobByIds(Set<Long> ids) {
        removeBatchByIds(ids);
        // 删除缓存
        ids.forEach(this::delCaches);
    }

    @Override
    public void downloadJobExcel(List<JobDO> jobDOS, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (JobDO jobDO : jobDOS) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("岗位名称", jobDO.getName());
            map.put("岗位状态", jobDO.getEnabled() ? "启用" : "停用");
            map.put("创建日期", jobDO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    public void delCaches(Long id) {
        redisHelper.del(RedisKeyConst.JOB_ID + id);
    }
}
