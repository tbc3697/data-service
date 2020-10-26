package pub.tbc.data.common.scheduled.custom.config;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Author tbc by 2020-10-25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CustomSpringSchedulerManagerRegistrar.class)
public @interface EnableCustomSpringScheduler {

    /**
     * 是否启动后台自动刷新：默认启用，暂不支持禁用
     *
     * @return
     */
    @AliasFor("value")
    boolean autoFlush() default true;

    @AliasFor("autoFlush")
    boolean value() default true;
}
