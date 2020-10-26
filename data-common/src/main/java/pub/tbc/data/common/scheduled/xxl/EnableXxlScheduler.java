package pub.tbc.data.common.scheduled.xxl;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author tbc by 2020-10-25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(XxlAdapterConfiguration.class)
public @interface EnableXxlScheduler {
}
