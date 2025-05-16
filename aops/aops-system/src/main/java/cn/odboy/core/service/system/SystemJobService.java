package cn.odboy.core.service.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.dal.dataobject.system.JobDO;
import cn.odboy.core.service.system.dto.JobCreateArgs;
import cn.odboy.core.service.system.dto.JobQueryArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemJobService extends IService<JobDO> {
    /**
     * 分页查询
     *
     * @param args 条件
     * @param page 分页参数
     * @return /
     */
    PageResult<JobDO> describeJobPage(JobQueryArgs args, Page<Object> page);

    /**
     * 查询全部数据
     *
     * @param args /
     * @return /
     */
    List<JobDO> describeJobList(JobQueryArgs args);


    /**
     * 验证是否被用户关联
     *
     * @param ids /
     */
    void verifyBindRelationByIds(Set<Long> ids);

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    JobDO describeJobById(Long id);

    /**
     * 创建
     *
     * @param args /
     */
    void saveJob(JobCreateArgs args);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyJobById(JobDO resources);

    /**
     * 删除
     *
     * @param ids /
     */
    void removeJobByIds(Set<Long> ids);

    /**
     * 导出数据
     *
     * @param jobDOS   待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadJobExcel(List<JobDO> jobDOS, HttpServletResponse response) throws IOException;

}
