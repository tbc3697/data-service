package pub.tbc.data.common.executor;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 注册全局线程池，并设置其状态为启用
 *
 * @Author tbc by 2020-10-25
 */
public class GlobalExecutorImportSelector implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        GlobalExecutorUtil.enabledGlobalIoThreadPool = true;
        registry.registerBeanDefinition(GlobalExecutorUtil.BEAN_NAME, new RootBeanDefinition(GlobalIoExecutorFactoryBean.class));
    }
}
