package cn.odboy.core.dal.mysql.log;

import cn.odboy.core.dal.dataobject.log.OperationLogDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLogDO> {
}
