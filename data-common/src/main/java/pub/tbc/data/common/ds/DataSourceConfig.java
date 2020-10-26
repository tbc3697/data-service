package pub.tbc.data.common.ds;

import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * contract 数据源配置类
 *
 * @author tbc
 * @date 2020-10-25
 **/
@Configuration
@MapperScan(basePackages = DataSourceConfig.PACKAGE, sqlSessionFactoryRef = "sqlSessionFactory")
public class DataSourceConfig {
    static final String PACKAGE = "pub.tbc.data.job.mapper.contract";
    private static final String MAPPER_LOCATION = "classpath*:pub/tbc/data/**/mapper/*.xml";

    @Bean(name = "dynamicDataSource")
    public DataSource dynamicDataSource(DataSourceProperties dsProperties) {
        // 若是权限放开，就可以直接使用合约的 apollo 中的数据源配置动态创建数据源，新增不需要重启
        Map<Object, Object> dataSourceMap = Maps.newHashMap();
        dsProperties.getContract().forEach((k, v) -> {
            String key = dataSourceKeyFormat(k);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(v.getUrl());
            config.setUsername(v.getUsername());
            config.setPassword(v.getPassword());
            HikariDataSource dataSource = new HikariDataSource(config);
            dataSourceMap.put(key, dataSource);
        });
        return new ContractDynamicDataSource(dataSourceMap);
    }

    private String dataSourceKeyFormat(String dataSourceKey) {
        StringBuilder builder = new StringBuilder();
        char[] chars = dataSourceKey.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '-') {
                char c = chars[++i];
                if (c >= 'a' && c <= 'z') {
                    builder.append((char) (c - 32));
                }
            } else {
                builder.append(chars[i]);
            }
        }
        return builder.toString();
    }

    private static SqlSessionFactoryBean createSqlSessionFactoryBean(final DataSource dataSource,
                                                                     final String[] mapperLocations) {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory;
    }

    protected static SqlSessionFactory createSqlSessionFactory(final DataSource dataSource, final String mapperLocation)
            throws Exception {
        return createSqlSessionFactory(dataSource, new String[]{mapperLocation});
    }

    private static SqlSessionFactory createSqlSessionFactory(final DataSource dataSource, final String[] mapperLocations)
            throws Exception {
        return createSqlSessionFactoryBean(dataSource, mapperLocations).getObject();
    }

    protected static SqlSessionTemplate createSqlSessionTemplate(final SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory contractSqlSessionFactory(@Qualifier("dynamicDataSource") final DataSource dataSource)
            throws Exception {
        return createSqlSessionFactory(dataSource, MAPPER_LOCATION);
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate contractSqlSessionTemplate(@Qualifier("sqlSessionFactory") final SqlSessionFactory sqlSessionFactory) {
        return createSqlSessionTemplate(sqlSessionFactory);
    }


    class ContractDynamicDataSource extends AbstractRoutingDataSource {

        public ContractDynamicDataSource(Map<Object, Object> targetDataSources) {
            setTargetDataSources(targetDataSources);
        }

        @Override
        protected Object determineCurrentLookupKey() {
            return null;
        }
    }


}
