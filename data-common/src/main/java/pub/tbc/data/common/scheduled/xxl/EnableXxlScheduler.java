package pub.tbc.data.common.scheduled.xxl;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 xxl-job 任务调度的适配器
 *
 * @Author tbc by 2020-10-25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(XxlAdapterConfiguration.class)
public @interface EnableXxlScheduler {
}
