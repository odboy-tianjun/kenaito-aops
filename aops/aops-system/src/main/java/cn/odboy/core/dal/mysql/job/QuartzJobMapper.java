package cn.odboy.core.dal.mysql.job;

import cn.odboy.core.dal.dataobject.job.QuartzJobDO;
import cn.odboy.core.service.system.dto.QuartzJobQueryArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface QuartzJobMapper extends BaseMapper<QuartzJobDO> {
    IPage<QuartzJobDO> queryQuartzJobPageByArgs(@Param("args") QuartzJobQueryArgs args, Page<Object> page);

    List<QuartzJobDO> queryActiveQuartzJobList();
}
