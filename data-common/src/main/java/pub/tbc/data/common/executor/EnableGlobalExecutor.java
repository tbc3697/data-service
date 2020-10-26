package pub.tbc.data.common.executor;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用全局线程池：线程是重资源，不需要自动装配，只要按需装配即可，后续会在本注解和配置中加线程参数
 *
 * @Author tbc by 2020-10-25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(GlobalExecutorImportSelector.class)
public @interface EnableGlobalExecutor {
}
