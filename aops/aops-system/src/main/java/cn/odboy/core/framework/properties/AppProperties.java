package cn.odboy.core.framework.properties;

import cn.odboy.core.framework.properties.dto.ContentRsaEncodeSettingModel;
import cn.odboy.core.framework.properties.dto.FileUploadSettingModel;
import cn.odboy.core.framework.properties.dto.JwtAuthSettingModel;
import cn.odboy.core.framework.properties.dto.QuartzTaskThreadPoolSettingModel;
import cn.odboy.core.framework.properties.dto.SwaggerApiDocSettingModel;
import cn.odboy.core.framework.properties.dto.UserLoginSettingModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用配置
 *
 * @author odboy
 * @date 2025-04-13
 */
@Data
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


