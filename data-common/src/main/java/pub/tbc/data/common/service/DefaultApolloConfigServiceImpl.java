package pub.tbc.data.common.service;

import com.ctrip.framework.apollo.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

/**
 * 默认基于 Apollo 的配置实现
 *
 * @Author tbc by 2020-10-25
 */
@Slf4j
@Service
public class DefaultApolloConfigServiceImpl implements ConfigService {
    private final String APOLLO_NAMESPACE = "application";

    public <T> T apolloGetProperty(String namespace, String key, String defaultValue, Function<String, T> function) {
        Config config = com.ctrip.framework.apollo.ConfigService.getConfig(namespace);
        String value = config.getProperty(key, defaultValue);
        return function.apply(value);
    }

    @Override
    public Optional<String> getProperty(String key) {
        return Optional.ofNullable(apolloGetProperty(APOLLO_NAMESPACE, key, "", Function.identity()));
    }

    /**
     * 同步位置提前量：基于时间（秒） -- sync.position.lead.time : 3
     */
    @Override
    public int leadPositionByTime() {
        return apolloGetProperty(APOLLO_NAMESPACE, "sync.position.lead.time", "3", Integer::valueOf);
    }

    /**
     * 同步提前量：基于ID -- sync.position.lead.id
     */
    @Override
    public int leadPositionById() {
        return apolloGetProperty(APOLLO_NAMESPACE, "sync.position.lead.id", "10", Integer::valueOf);
    }

    /**
     * 单次同步数量  - sync.limit.count: 500
     */
    @Override
    public int syncLimitCount() {
        return apolloGetProperty(APOLLO_NAMESPACE, "sync.limit.count", "500", Integer::valueOf);
    }

    /**
     * 单次同步数量，最大扩容次数 - sync.limit.expand.max: 5
     */
    @Override
    public int syncLimitExpandMax() {
        return apolloGetProperty(APOLLO_NAMESPACE, "sync.limit.expand.max", "5", Integer::valueOf);
    }

    /**
     * spring scheduler 线程数量 - spring.scheduler.thread.count : 20
     */
    @Override
    public int schedulerPoolSize() {
        return apolloGetProperty(APOLLO_NAMESPACE, "spring.scheduler.thread.count", "20", Integer::valueOf);
    }

    @Override
    public boolean taskAutoFlush() {
        return apolloGetProperty(APOLLO_NAMESPACE, "application.task.autoFlush", "0", Integer::valueOf) == 1;
    }
}
