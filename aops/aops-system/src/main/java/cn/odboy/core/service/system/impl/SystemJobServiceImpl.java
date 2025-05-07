package cn.odboy.core.service.system.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.dal.dataobject.system.Job;
import cn.odboy.core.dal.mysql.system.JobMapper;
import cn.odboy.core.service.system.SystemJobService;
import cn.odboy.core.service.system.dto.CreateJobRequest;
import cn.odboy.exception.EntityExistException;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.FileUtil;
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

@Service
@RequiredArgsConstructor
public class SystemJobServiceImpl extends ServiceImpl<JobMapper, Job> implements SystemJobService {
    private final JobMapper jobMapper;
    private final RedisHelper redisHelper;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveJob(CreateJobRequest resources) {
        Job job = jobMapper.getJobByName(resources.getName());
        if (job != null) {
            throw new EntityExistException(Job.class, "name", resources.getName());
        }
        save(BeanUtil.copyProperties(resources, Job.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyJobById(Job resources) {
        Job job = getById(resources.getId());
        Job old = jobMapper.getJobByName(resources.getName());
        if (old != null && !old.getId().equals(resources.getId())) {
            throw new EntityExistException(Job.class, "name", resources.getName());
        }
        resources.setId(job.getId());
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
    public void downloadJobExcel(List<Job> jobs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Job job : jobs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("岗位名称", job.getName());
            map.put("岗位状态", job.getEnabled() ? "启用" : "停用");
            map.put("创建日期", job.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    public void delCaches(Long id) {
        redisHelper.del(SystemRedisKey.JOB_ID + id);
    }
}