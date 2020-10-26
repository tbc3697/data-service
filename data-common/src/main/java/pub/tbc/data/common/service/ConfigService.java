package pub.tbc.data.common.service;

import java.util.Optional;

/**
 * global config
 *
 * @Author tbc by 2020-10-25
 */
public interface ConfigService {

    /**
     * 获取配置属性值
     *
     * @param key
     * @return
     */
    Optional<String> getProperty(String key);

    /**
     * 同步位置提前量：时间，秒 -- sync.position.lead.time:
     *
     * @return
     */
    default int leadPositionByTime() {
        return 3;
    }

    /**
     * 同步提前量：ID -- sync.position.lead.id
     *
     * @return
     */
    default int leadPositionById() {
        return 10;
    }

    /**
     * 单次同步数量  - sync.limit.count: 500
     *
     * @return
     */
    default int syncLimitCount() {
        return 500;
    }

    /**
     * 单次同步数量，最大扩容次数 - sync.limit.expand.max: 5
     */
    default int syncLimitExpandMax() {
        return 10;
    }

    /**
     * spring ThreadPoolTaskScheduler 调度框架线程数量
     */
    default int schedulerPoolSize() {
        return 20;
    }

    boolean taskAutoFlush();



}
