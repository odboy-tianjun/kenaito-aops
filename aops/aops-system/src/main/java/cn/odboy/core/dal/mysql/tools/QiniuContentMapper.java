package cn.odboy.core.dal.mysql.tools;

import cn.odboy.core.dal.dataobject.tools.QiniuContentDO;
import cn.odboy.core.service.tools.dto.QueryQiniuArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface QiniuContentMapper extends BaseMapper<QiniuContentDO> {
    QiniuContentDO getQiniuContentByName(@Param("name") String name);

    IPage<QiniuContentDO> queryQiniuContentPageByArgs(@Param("args") QueryQiniuArgs args, Page<Object> page);
}