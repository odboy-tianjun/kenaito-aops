package cn.odboy.core.dal.mysql.tools;

import cn.odboy.core.dal.dataobject.tools.LocalStorageDO;
import cn.odboy.core.service.tools.dto.LocalStorageQueryArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LocalStorageMapper extends BaseMapper<LocalStorageDO> {
    IPage<LocalStorageDO> queryLocalStoragePageByArgs(@Param("args") LocalStorageQueryArgs args, Page<Object> page);
}
