package pub.tbc.data.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pub.tbc.data.common.executor.EnableGlobalExecutor;
import pub.tbc.data.common.scheduled.custom.config.EnableCustomSpringScheduler;

/**
 * @Author tbc by 2020-10-25
 */
@Slf4j
@SpringBootApplication
@EnableGlobalExecutor
// @EnableXxlScheduler
@EnableCustomSpringScheduler
public class DataJobApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .sources(DataJobApplication.class)
                .logStartupInfo(true)
                .web(WebApplicationType.SERVLET)
                .run(args);
        log.info("系统启动完成 >>>>>>>>>");
    }
}
