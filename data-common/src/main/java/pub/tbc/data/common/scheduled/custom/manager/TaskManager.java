package pub.tbc.data.common.scheduled.custom.manager;

import org.springframework.context.ConfigurableApplicationContext;
import pub.tbc.data.common.domain.DataTask;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @Author tbc by 2020-10-25
 */
public interface TaskManager {

    /**
     * 初始化所有任务
     *
     * @param context
     * @return
     */
    void initTask(ConfigurableApplicationContext context);

    /**
     * 启用任务自动刷新机制（后台线程）
     *
     * @param context
     */
    void enableTaskAutoFlush(ConfigurableApplicationContext context);

    /**
     * 禁用任务自动刷新机制（后台线程）
     *
     * @param context
     */
    void disableTaskAutoFlush();

    /**
     * 初始化指定任务，任务必须跟数据库中记录匹配
     *
     * @param task
     * @return
     */
    void initTask(DataTask task);

    /**
     * 根据任务名称查询Future
     *
     * @param taskName
     * @return
     */
    ScheduledFuture<?> scheduledFuture(String taskName);

    Map<String, ScheduledFuture<?>> scheduledFuture();

    /**
     * 从线程池中移除任务
     *
     * @return
     */
    boolean removeTask(String taskName);


}
