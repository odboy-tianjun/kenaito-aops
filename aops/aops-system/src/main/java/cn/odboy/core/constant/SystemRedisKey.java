package cn.odboy.core.constant;

/**
 * 关于缓存的Key集合
 */
public interface SystemRedisKey {

    /**
     * 用户
     */
    String USER_ID = "user::id:";

    /**
     * 数据
     */
    String DATA_USER = "data::user:";

    /**
     * 菜单
     */
    String MENU_ID = "menu::id:";
    String MENU_USER = "menu::user:";

    /**
     * 角色授权
     */
    String ROLE_AUTH = "role::auth:";
    String ROLE_USER = "role::user:";

    /**
     * 角色信息
     */
    String ROLE_ID = "role::id:";

    /**
     * 部门
     */
    String DEPT_ID = "dept::id:";

    /**
     * 岗位
     */
    String JOB_ID = "job::id:";

    /**
     * 数据字典
     */
    String DICT_NAME = "dict::name:";

    /**
     * 在线用户
     */
    String ONLINE_USER = "online::token:";

    /**
     * 登录验证码
     */
    String CAPTCHA_LOGIN = "captcha::login:";

    /**
     * 用户信息
     */
    String USER_INFO = "user::info:";
}
