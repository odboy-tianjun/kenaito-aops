package cn.odboy;

import cn.odboy.annotation.AnonymousGetMapping;
import cn.odboy.context.BootApplication;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import java.net.UnknownHostException;

@Slf4j
@RestController
@Api(hidden = true)
@SpringBootApplication
public class AppRun extends BootApplication {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication springApplication = new SpringApplication(AppRun.class);
        inited(springApplication.run(args));
    }

    /**
     * 访问首页提示
     *
     * @return /
     */
    @AnonymousGetMapping("/")
    public String index() {
        return "Backend service started successfully";
    }
}
