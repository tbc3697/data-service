package pub.tbc.data.common.scheduled.custom.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 任务自动刷新功能注册
 *
 * @Author tbc by 2020-10-25
 */
public class CustomSpringSchedulerAuthFlushInitializerRegistrar implements ImportBeanDefinitionRegistrar {
    private final String INIT_BEAN_NAME = "customSpringSchedulerAutoFlushInitializer";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinition initBeanDefinition = new RootBeanDefinition(CustomSpringSchedulerAutoFlushInitializer.class);
        registry.registerBeanDefinition(INIT_BEAN_NAME, initBeanDefinition);
    }
}
