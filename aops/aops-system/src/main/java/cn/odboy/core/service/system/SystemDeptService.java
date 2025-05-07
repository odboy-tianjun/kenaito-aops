package cn.odboy.core.service.system;

import cn.odboy.core.dal.dataobject.system.Dept;
import cn.odboy.core.service.system.dto.CreateDeptRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface SystemDeptService extends IService<Dept> {

    /**
     * 创建
     *
     * @param resources /
     */
    void saveDept(CreateDeptRequest resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyDept(Dept resources);

    /**
     * 删除
     *
     * @param depts /
     */
    void removeDeptByIds(Set<Dept> depts);

    /**
     * 导出数据
     *
     * @param depts    待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadDeptExcel(List<Dept> depts, HttpServletResponse response) throws IOException;
}