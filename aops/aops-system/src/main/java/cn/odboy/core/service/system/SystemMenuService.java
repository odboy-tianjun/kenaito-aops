package cn.odboy.core.service.system;

import cn.odboy.core.controller.system.vo.MenuVo;
import cn.odboy.core.dal.dataobject.system.MenuDO;
import cn.odboy.core.service.system.dto.MenuQueryArgs;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemMenuService extends IService<MenuDO> {

    /**
     * 查询全部数据
     *
     * @param args    条件
     * @param isQuery /
     * @return /
     * @throws Exception /
     */
    List<MenuDO> describeMenuList(MenuQueryArgs args, Boolean isQuery) throws Exception;

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    MenuDO describeMenuById(long id);

    /**
     * 获取所有子节点，包含自身ID
     *
     * @param menuDOList /
     * @param menuDOSet  /
     * @return /
     */
    Set<MenuDO> describeChildMenuSet(List<MenuDO> menuDOList, Set<MenuDO> menuDOSet);

    /**
     * 构建菜单树
     *
     * @param menuDOS 原始数据
     * @return /
     */
    List<MenuDO> buildMenuTree(List<MenuDO> menuDOS);

    /**
     * 构建菜单树
     *
     * @param menuDOS /
     * @return /
     */
    List<MenuVo> buildMenuResponse(List<MenuDO> menuDOS);

    /**
     * 懒加载菜单数据
     *
     * @param pid /
     * @return /
     */
    List<MenuDO> describeMenuListByPid(Long pid);

    /**
     * 根据ID获取同级与上级数据
     *
     * @param menuDO  /
     * @param objects /
     * @return /
     */
    List<MenuDO> describeSuperiorMenuList(MenuDO menuDO, List<MenuDO> objects);

    /**
     * 根据当前用户获取菜单
     *
     * @param currentUserId /
     * @return /
     */
    List<MenuDO> describeMenuListByUserId(Long currentUserId);

    /**
     * 创建
     *
     * @param resources /
     */
    void saveMenu(MenuDO resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyMenuById(MenuDO resources);


    /**
     * 删除
     *
     * @param menuDOSet /
     */
    void removeMenuByIds(Set<MenuDO> menuDOSet);

    /**
     * 导出
     *
     * @param menuDOS  待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadMenuExcel(List<MenuDO> menuDOS, HttpServletResponse response) throws IOException;


}
