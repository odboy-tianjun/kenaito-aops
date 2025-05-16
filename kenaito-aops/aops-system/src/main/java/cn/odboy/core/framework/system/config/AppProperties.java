package cn.odboy.core.framework.system.config;

import cn.odboy.core.framework.system.core.dto.ContentRsaEncodeSettingModel;
import cn.odboy.core.framework.system.core.dto.FileUploadSettingModel;
import cn.odboy.core.framework.system.core.dto.JwtAuthSettingModel;
import cn.odboy.core.framework.system.core.dto.QuartzTaskThreadPoolSettingModel;
import cn.odboy.core.framework.system.core.dto.SwaggerApiDocSettingModel;
import cn.odboy.core.framework.system.core.dto.UserLoginSettingModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用配置
 *
 * @author odboy
 * @date 2025-04-13
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private FileUploadSettingModel file;
    private ContentRsaEncodeSettingModel rsa;
    private JwtAuthSettingModel jwt;
    private UserLoginSettingModel login;
    private SwaggerApiDocSettingModel swagger;
    private QuartzTaskThreadPoolSettingModel asyncTaskPool;
}


