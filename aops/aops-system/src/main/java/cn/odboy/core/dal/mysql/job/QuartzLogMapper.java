package cn.odboy.core.dal.mysql.job;

import cn.odboy.core.service.system.dto.QueryQuartzJobRequest;
import cn.odboy.core.dal.dataobject.job.QuartzLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface QuartzLogMapper extends BaseMapper<QuartzLog> {
    IPage<QuartzLog> queryQuartzLogPageByArgs(@Param("criteria") QueryQuartzJobRequest criteria, Page<Object> page);
}
