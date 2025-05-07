package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.service.system.dto.QueryDictRequest;
import cn.odboy.core.dal.dataobject.system.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DictMapper extends BaseMapper<Dict> {
    IPage<Dict> queryDictPageByArgs(@Param("criteria") QueryDictRequest criteria, Page<Object> page);
}