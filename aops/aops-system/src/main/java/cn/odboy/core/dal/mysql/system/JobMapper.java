package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.service.system.dto.QueryJobRequest;
import cn.odboy.core.dal.dataobject.system.Job;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface JobMapper extends BaseMapper<Job> {
    Job getJobByName(@Param("name") String name);

    IPage<Job> queryJobPageByArgs(@Param("criteria") QueryJobRequest criteria, Page<Object> page);
}