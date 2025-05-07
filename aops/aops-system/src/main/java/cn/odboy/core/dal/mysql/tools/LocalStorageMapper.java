package cn.odboy.core.dal.mysql.tools;

import cn.odboy.core.service.tools.dto.QueryLocalStorageRequest;
import cn.odboy.core.dal.dataobject.tools.LocalStorage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LocalStorageMapper extends BaseMapper<LocalStorage> {
    IPage<LocalStorage> queryLocalStoragePageByArgs(@Param("criteria") QueryLocalStorageRequest criteria, Page<Object> page);
}
