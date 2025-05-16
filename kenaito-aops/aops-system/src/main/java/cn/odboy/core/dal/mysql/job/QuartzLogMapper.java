package cn.odboy.core.dal.mysql.job;

import cn.odboy.core.dal.dataobject.job.QuartzLogDO;
import cn.odboy.core.service.system.dto.QuartzJobQueryArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface QuartzLogMapper extends BaseMapper<QuartzLogDO> {
    IPage<QuartzLogDO> queryQuartzLogPageByArgs(@Param("args") QuartzJobQueryArgs args, Page<Object> page);
}
