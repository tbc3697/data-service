package pub.tbc.data.common.ds;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author tbc by 2020-10-25
 */
@Data
@Component
@ConfigurationProperties(prefix = "datasource", ignoreInvalidFields = true)
public class DataSourceProperties {
    private Map<String, DataSourceProperty> contract = Maps.newHashMap();

    @Data
    public static class DataSourceProperty {
        private String driverClassName;
        private String url;
        private String username;
        private String password;
    }
}
