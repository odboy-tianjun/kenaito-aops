package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.dal.dataobject.system.DictDetail;
import cn.odboy.core.service.system.dto.QueryDictDetailArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Set;


@Mapper
public interface DictDetailMapper extends BaseMapper<DictDetail> {
    List<DictDetail> queryDictDetailListByDictName(@Param("name") String name);

    IPage<DictDetail> queryDictDetailPageByArgs(@Param("args") QueryDictDetailArgs args, Page<Object> page);

    void deleteDictDetailByDictIds(@Param("dictIds") Set<Long> dictIds);
}