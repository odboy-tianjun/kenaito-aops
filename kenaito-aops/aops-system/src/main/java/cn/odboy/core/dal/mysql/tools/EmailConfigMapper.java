package cn.odboy.core.dal.mysql.tools;

import cn.odboy.core.dal.dataobject.tools.EmailConfigDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface EmailConfigMapper extends BaseMapper<EmailConfigDO> {
}
