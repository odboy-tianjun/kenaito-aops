package cn.odboy.core.api.system.impl;

import cn.odboy.base.PageResult;
import cn.odboy.core.api.system.SystemQuartzJobApi;
import cn.odboy.core.dal.dataobject.job.QuartzJob;
import cn.odboy.core.dal.dataobject.job.QuartzLog;
import cn.odboy.core.dal.mysql.job.QuartzJobMapper;
import cn.odboy.core.dal.mysql.job.QuartzLogMapper;
import cn.odboy.core.service.system.dto.QueryQuartzJobRequest;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemQuartzJobApiImpl implements SystemQuartzJobApi {
    private final QuartzJobMapper quartzJobMapper;
    private final QuartzLogMapper quartzLogMapper;

    @Override
    public PageResult<QuartzJob> describeQuartzJobPage(QueryQuartzJobRequest criteria, Page<Object> page) {
        return PageUtil.toPage(quartzJobMapper.queryQuartzJobPageByArgs(criteria, page));
    }

    @Override
    public PageResult<QuartzLog> describeQuartzLogPage(QueryQuartzJobRequest criteria, Page<Object> page) {
        return PageUtil.toPage(quartzLogMapper.queryQuartzLogPageByArgs(criteria, page));
    }

    @Override
    public List<QuartzJob> describeQuartzJobList(QueryQuartzJobRequest criteria) {
        return quartzJobMapper.queryQuartzJobPageByArgs(criteria, PageUtil.getCount(quartzJobMapper)).getRecords();
    }

    @Override
    public List<QuartzLog> describeQuartzLogList(QueryQuartzJobRequest criteria) {
        return quartzLogMapper.queryQuartzLogPageByArgs(criteria, PageUtil.getCount(quartzLogMapper)).getRecords();
    }
}
