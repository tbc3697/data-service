package pub.tbc.data.common.scheduled.custom.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用任务自动刷新
 *
 * @Author tbc by 2020-10-25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CustomSpringSchedulerAuthFlushInitializerRegistrar.class)
public @interface EnableTaskAutoFlush {
}
