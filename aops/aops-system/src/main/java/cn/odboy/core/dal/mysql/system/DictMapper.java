package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.dal.dataobject.system.DictDO;
import cn.odboy.core.service.system.dto.QueryDictArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DictMapper extends BaseMapper<DictDO> {
    IPage<DictDO> queryDictPageByArgs(@Param("args") QueryDictArgs args, Page<Object> page);
}