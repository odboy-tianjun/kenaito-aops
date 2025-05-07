package cn.odboy.core.service.system;

import cn.odboy.core.dal.dataobject.system.Role;
import cn.odboy.core.service.system.dto.CreateRoleRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemRoleService extends IService<Role> {


    /**
     * 创建
     *
     * @param resources /
     */
    void saveRole(CreateRoleRequest resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyRoleById(Role resources);

    /**
     * 修改绑定的菜单
     *
     * @param resources /
     */
    void modifyBindMenuById(Role resources);

    /**
     * 删除
     *
     * @param ids /
     */
    void removeRoleByIds(Set<Long> ids);


    /**
     * 导出数据
     *
     * @param roles    待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadRoleExcel(List<Role> roles, HttpServletResponse response) throws IOException;

}
