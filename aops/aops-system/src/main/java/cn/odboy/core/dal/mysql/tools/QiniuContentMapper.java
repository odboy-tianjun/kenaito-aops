package cn.odboy.core.dal.mysql.tools;

import cn.odboy.core.service.tools.dto.QueryQiniuRequest;
import cn.odboy.core.dal.dataobject.tools.QiniuContent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface QiniuContentMapper extends BaseMapper<QiniuContent> {
    QiniuContent getQiniuContentByName(@Param("name") String name);

    IPage<QiniuContent> queryQiniuContentPageByArgs(@Param("criteria") QueryQiniuRequest criteria, Page<Object> page);
}