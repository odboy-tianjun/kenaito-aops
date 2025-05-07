package cn.odboy.core.service.system;

import cn.odboy.core.dal.dataobject.system.Job;
import cn.odboy.core.service.system.dto.CreateJobRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemJobService extends IService<Job> {
    /**
     * 创建
     *
     * @param resources /
     */
    void saveJob(CreateJobRequest resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyJobById(Job resources);

    /**
     * 删除
     *
     * @param ids /
     */
    void removeJobByIds(Set<Long> ids);

    /**
     * 导出数据
     *
     * @param jobs     待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadJobExcel(List<Job> jobs, HttpServletResponse response) throws IOException;

}