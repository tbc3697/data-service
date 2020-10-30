package pub.tbc.data.common.scheduled.custom.config;

import pub.tbc.data.common.scheduled.custom.manager.DefaultTaskManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Author tbc by 2020-10-25
 */
public class CustomSpringSchedulerManagerRegistrar implements ImportBeanDefinitionRegistrar {

    private final String MANAGER_BEAN_NAME = "customSpringSchedulerManager";
    private final String INIT_BEAN_NAME = "customSpringSchedulerInitializer";


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 注册调度管理器
        BeanDefinition managerBeanDefinition = new RootBeanDefinition(DefaultTaskManager.class);
        registry.registerBeanDefinition(MANAGER_BEAN_NAME, managerBeanDefinition);
        // 注册初始化
        BeanDefinition initBeanDefinition = new RootBeanDefinition(CustomSpringSchedulerInitializer.class);
        registry.registerBeanDefinition(INIT_BEAN_NAME, initBeanDefinition);
    }
}
