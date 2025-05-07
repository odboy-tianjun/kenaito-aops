package cn.odboy.core.api.system;

import cn.odboy.core.dal.dataobject.system.Menu;
import cn.odboy.core.dal.model.MenuResponse;
import cn.odboy.core.service.system.dto.QueryMenuRequest;
import java.util.List;
import java.util.Set;

public interface SystemMenuApi {
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
}
