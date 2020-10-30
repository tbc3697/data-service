package pub.tbc.data.common.scheduled.custom.config;

import pub.tbc.data.common.scheduled.custom.manager.TaskManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;

import javax.inject.Inject;

/**
 * 开启任务自动刷新功能
 *
 * @Author tbc by 2020-10-25
 */
public class CustomSpringSchedulerAutoFlushInitializer implements ApplicationRunner {

    @Inject
    private TaskManager taskManager;

    @Inject
    private ConfigurableApplicationContext context;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        taskManager.enableTaskAutoFlush(context);
    }


}
