package cn.odboy.core.service.system;

import cn.odboy.core.dal.dataobject.system.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemMenuService extends IService<Menu> {


    /**
     * 创建
     *
     * @param resources /
     */
    void saveMenu(Menu resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyMenuById(Menu resources);


    /**
     * 删除
     *
     * @param menuSet /
     */
    void removeMenuByIds(Set<Menu> menuSet);

    /**
     * 导出
     *
     * @param menus    待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadMenuExcel(List<Menu> menus, HttpServletResponse response) throws IOException;


}
