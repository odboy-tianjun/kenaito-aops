package cn.odboy.core.framework.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.servlet.MultipartConfigElement;
import java.io.File;


@Slf4j
@Configuration
public class MultipartConfig {
    @Value("${spring.application.name}")
    private String name;

    /**
     * 文件上传临时路径
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        String location = System.getProperty("user.home") + "/." + name + "/tmp";
        File tmpFile = new File(location);
        if (!tmpFile.exists()) {
            if (!tmpFile.mkdirs()) {
                log.error("创建临时文件失败");
            }
        }
        factory.setLocation(location);
        return factory.createMultipartConfig();
    }
}