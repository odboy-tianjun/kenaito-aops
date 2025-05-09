package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.dal.dataobject.system.JobDO;
import cn.odboy.core.service.system.dto.QueryJobArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface JobMapper extends BaseMapper<JobDO> {
    JobDO getJobByName(@Param("name") String name);

    IPage<JobDO> queryJobPageByArgs(@Param("args") QueryJobArgs args, Page<Object> page);
}