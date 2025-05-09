package cn.odboy.core.service.system;

import cn.odboy.core.controller.system.vo.MenuResponse;
import cn.odboy.core.dal.dataobject.system.Menu;
import cn.odboy.core.service.system.dto.QueryMenuRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemMenuService extends IService<Menu> {

    /**
     * 查询全部数据
     *
     * @param criteria 条件
     * @param isQuery  /
     * @return /
     * @throws Exception /
     */
    List<Menu> describeMenuList(QueryMenuRequest criteria, Boolean isQuery) throws Exception;

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    Menu describeMenuById(long id);
    /**
     * 获取所有子节点，包含自身ID
     *
     * @param menuList /
     * @param menuSet  /
     * @return /
     */
    Set<Menu> describeChildMenuSet(List<Menu> menuList, Set<Menu> menuSet);

    /**
     * 构建菜单树
     *
     * @param menus 原始数据
     * @return /
     */
    List<Menu> buildMenuTree(List<Menu> menus);

    /**
     * 构建菜单树
     *
     * @param menus /
     * @return /
     */
    List<MenuResponse> buildMenuResponse(List<Menu> menus);
    /**
     * 懒加载菜单数据
     *
     * @param pid /
     * @return /
     */
    List<Menu> describeMenuListByPid(Long pid);

    /**
     * 根据ID获取同级与上级数据
     *
     * @param menu    /
     * @param objects /
     * @return /
     */
    List<Menu> describeSuperiorMenuList(Menu menu, List<Menu> objects);

    /**
     * 根据当前用户获取菜单
     *
     * @param currentUserId /
     * @return /
     */
    List<Menu> describeMenuListByUserId(Long currentUserId);
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
