package cn.odboy.core.api.tools;

import cn.odboy.base.PageResult;
import cn.odboy.core.dal.dataobject.tools.QiniuContent;
import cn.odboy.core.service.tools.dto.QueryQiniuRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface QiniuContentApi {

    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<QiniuContent> describeQiniuContentPage(QueryQiniuRequest criteria, Page<Object> page);

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @return /
     */
    List<QiniuContent> describeQiniuContentList(QueryQiniuRequest criteria);
}
