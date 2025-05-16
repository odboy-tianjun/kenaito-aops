package cn.odboy.util;

import cn.odboy.core.framework.mybatisplus.mybatis.core.util.CmdGenHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CodeGenTests {
    @Value("${spring.datasource.druid.url}")
    private String url;
    @Value("${spring.datasource.druid.driverClassName}")
    private String driverClassName;
    @Value("${spring.datasource.druid.username}")
    private String username;
    @Value("${spring.datasource.druid.password}")
    private String password;


    @Test
    public void doCodeGen() {
        CmdGenHelper generator = new CmdGenHelper();
        generator.setDatabaseUrl(url);
        generator.setDriverClassName(driverClassName);
        generator.setDatabaseUsername(username);
        generator.setDatabasePassword(password);
        generator.gen("", List.of(
//                "aops_app",
//                "aops_app_user",
//                "aops_app_user_collect",
//                "aops_kubernetes_cluster_config",
//                "aops_kubernetes_cluster_node",
//                "aops_kubernetes_containerd_spec_config",
//                "aops_kubernetes_network_ingress",
//                "aops_kubernetes_network_service"
                "operation_log"
        ));
    }
}
