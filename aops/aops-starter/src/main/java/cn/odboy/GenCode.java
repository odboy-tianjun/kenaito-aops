package cn.odboy;


import cn.odboy.core.framework.mybatisplus.util.CmdGenHelper;
import java.util.List;

/**
 * 代码生成入口
 *
 * @date 2024-04-27
 */
public class GenCode {
    public static void main(String[] args) {
        CmdGenHelper generator = new CmdGenHelper();
        generator.setDatabaseUrl(String.format("jdbc:mysql://%s:%s/%s", "192.168.235.102", 3308, "kenaito_aops"));
        generator.setDriverClassName("com.mysql.cj.jdbc.Driver");
        generator.setDatabaseUsername("root");
        generator.setDatabasePassword("123456");
        genCareer(generator);
    }

    private static void genCareer(CmdGenHelper generator) {
        generator.gen("", List.of(
                "aops_app",
                "aops_app_user",
                "aops_app_user_collect",
                "aops_kubernetes_cluster_config",
                "aops_kubernetes_cluster_node",
                "aops_kubernetes_containerd_spec_config",
                "aops_kubernetes_network_ingress",
                "aops_kubernetes_network_service"
        ));
    }
}
