package pub.tbc.data.common.scheduled.xxl;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Author tbc by 2020/9/30 2020-10-25
 */
public class XxlAdapterConfiguration implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{XxlJobHandler.class.getName()};
    }
}
