package pub.tbc.data.common.scheduled.custom.manager;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import pub.tbc.data.common.domain.DataTask;
import pub.tbc.data.common.domain.DataTaskMapper;
import pub.tbc.data.common.scheduled.IDataTask;
import pub.tbc.data.common.scheduled.TaskResult;
import pub.tbc.data.common.service.ConfigService;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/**
 * @Author tbc by 2020-10-25
 */
@Slf4j
public class DefaultTaskManager implements TaskManager {
    private final Integer DEFAULT_WORK_ID = 0;

    private static AtomicBoolean scheduleInitCompleted = new AtomicBoolean(false);

    /**
     * 记录开始时间
     */
    private final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<StopWatch> stopWatchLocal = new ThreadLocal<>();

    private Thread taskAuthFlushThread;


    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private DataTaskMapper taskMapper;

    @Inject
    private ConfigService configService;

    private ThreadPoolTaskScheduler taskScheduler;
    private Map<String, ScheduledFuture<?>> futureMap = Maps.newConcurrentMap();

    public Integer getWorkId() {
        String workIdStr = System.getProperty("workId");
        if (StringUtils.isEmpty(workIdStr)) {
            return DEFAULT_WORK_ID;
        }
        return Integer.valueOf(workIdStr);
    }

    private void initTaskScheduler() {
        if (scheduleInitCompleted.get()) {
            return;
        }
        if (scheduleInitCompleted.compareAndSet(false, true)) {
            log.info("初始化 spring taskScheduler");
            int poolSize = configService.schedulerPoolSize();
            taskScheduler = new ThreadPoolTaskScheduler();
            taskScheduler.setPoolSize(poolSize);
            taskScheduler.setThreadNamePrefix("UpexDataScheduler");
            taskScheduler.initialize();
            log.info("初始化 spring taskScheduler 完成，线程数量：{}", poolSize);
        }
    }

    private List<DataTask> getCandidateTasks(ConfigurableApplicationContext context) {
        return taskMapper.findEnableTasks();
    }

    private Predicate<DataTask> getTaskFilterBySystem() {
        String enableStr = System.getProperty("task.enable");
        // 未指定启用的任务时，启用全部
        if (StringUtils.isEmpty(enableStr) || "ALL".equalsIgnoreCase(enableStr)) {
            return a -> true;
        }
        List<String> enableTaskNames = Splitter.on(",").splitToList(enableStr)
                .stream()
                .map(String::toLowerCase)
                .collect(toList());
        return task -> enableTaskNames.contains(task.getTaskName());
    }

    /**
     * 统一包装要调度的任务，成功修改任务状态才会被执行
     *
     * @param dataTask
     * @param task
     * @return
     */
    private Runnable taskWrap(IDataTask dataTask, DataTask task) {
        return () -> {
            boolean continueExec = false;
            do {
                if (tryUpdateStatusForStart(task)) {
                    try {
                        StopWatch stopWatch = new StopWatch(task.getTaskName());
                        stopWatchLocal.set(stopWatch);
                        stopWatch.start();

                        startTimeThreadLocal.set(System.currentTimeMillis());
                        log.info("开始执行任务：{}", task.getTaskName());
                        TaskResult result = dataTask.execute();
                        continueExec = result.isContinueExec();
                    } finally {
                        // 任务结束要恢复状态
                        // TODO ： 此处要考虑下，是直接恢复状态，还是再判断下 continueExec，如果需要继续执行任务，可暂不恢复状态
                        finishTask(task);
                    }
                } else {
                    continueExec = false;
                    log.info("同步任务 [{}] 正在执行中，本次任务中止", task.getTaskName());
                }
            } while (continueExec);
        };
    }

    private void finishTask(DataTask task) {
        try {
            Long startLong = startTimeThreadLocal.get();
            Long endLong = System.currentTimeMillis();
            LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startLong), ZoneId.systemDefault());
            LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endLong), ZoneId.systemDefault());
            long time = endLong - startLong;

            if (updateStatusForStop(task)) {
                log.info("任务 [{}] 执行结束, 开始时间：{}，结束时间：{}，耗时：{}", task.getTaskName(), startTime, endTime, time);
            } else {
                log.error("任务执行结束，恢复任务为可调度状态失败，数据异常，任务: {}", task.getTaskName());
            }
            stopWatchLocal.get().stop();
            log.info("\n{}", stopWatchLocal.get().prettyPrint());
        } catch (Exception e) {
            boolean isOk;
            log.error("修改状态发生异常，准备重试：{} - {}", e.getClass().getName(), e.getMessage());
            int i = 0, end = 3;
            do {
                if (isOk = updateStatusForStop(task)) {
                    break;
                }
                i++;
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ie) {
                    log.error("休眠时发生中断：{}", ie.getMessage(), ie);
                }
            } while (i < end);
            if (isOk) {
                log.info("任务 [{}] 执行结束", task.getTaskName());
            }
        }
    }

    /**
     * 尝试修改任务状态为 2（执行中）
     *
     * @param task
     * @return
     */
    private boolean tryUpdateStatusForStart(DataTask task) {
        int result = taskMapper.execTaskUpdateStatus(task.getId(), getWorkId());
        log.info("tryUpdateStatusStart ==> {}", result);
        return result == 1;
    }

    /**
     * @param task
     * @return
     */
    private boolean updateStatusForStop(DataTask task) {
        int result = taskMapper.finishTaskUpdateStatus(task.getId());
        log.info("updateStatusStop ==> {}", result);
        return result == 1;
    }

    private AtomicBoolean task1Executed = new AtomicBoolean(false);
    private AtomicBoolean task2Executed = new AtomicBoolean(false);

    private void sqlInit() {
        try {
            log.info("init begin ~~~");
            // 增加任务：统计
            if (task1Executed.compareAndSet(false, true)) {
                taskMapper.insert(new DataTask()
                        .setTaskName("delegateDealStatisticByDay")
                        .setCron("0 0/10 * * * ?")
                        .setExpireTime(90)
                        .setStatus(1)
                );
            }
            // 增加任务：清理
            if (task2Executed.compareAndSet(false, true)) {
                taskMapper.insert(new DataTask()
                        .setTaskName("contractDelegateDealCleaner")
                        .setCron("0 0/10 * * * ?")
                        .setExpireTime(20)
                        .setStatus(0)
                );
            }
            log.info("init end ~~~");
        } catch (Exception e) {
            log.error("ifInitSql ===> {}", e.getMessage());
        }
    }

    @Override
    public void enableTaskAutoFlush(ConfigurableApplicationContext context) {
        log.info("create taskAutoFlush Thread...");
        if (taskAuthFlushThread != null) {
            taskAuthFlushThread.interrupt();
        }
        taskAuthFlushThread = new Thread(() -> {
            while (true) {
                try {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    } else {
                        if (configService.taskAutoFlush()) {
                            doInitTask(context);
                        }
                    }
                    Thread.yield();
                    TimeUnit.MINUTES.sleep(3);
                } catch (InterruptedException e) {
                    log.info("自动刷新任务中断");
                    break;
                }
            }
        }, "task-auto-flush");
        log.info("taskAutoFlush Thread set daemon");
        taskAuthFlushThread.setDaemon(true);
        log.info("stating taskAutoFlush Thread");
        taskAuthFlushThread.start();
        log.info("stated taskAutoFlush Thread");
    }

    @Override
    public void disableTaskAutoFlush() {
        taskAuthFlushThread.interrupt();
    }

    /**
     * 初始化所有任务
     *
     * @param context
     */
    @Override
    public void initTask(ConfigurableApplicationContext context) {
        // if (configService.ifInitSql()) {
        //     sqlInit();
        // }
        log.info("开始加载调度任务");
        doInitTask(context);
        log.info("调度任务初始化完成");
    }

    private void doInitTask(ConfigurableApplicationContext context) {
        // 数据库中的候选任务
        List<DataTask> candidateTasks = getCandidateTasks(context);
        if (CollectionUtils.isEmpty(candidateTasks)) {
            log.info("没有候选任务");
            return;
        }
        // 按系统参数来一遍过滤（调试方便）
        List<DataTask> realTasks = candidateTasks.stream().filter(getTaskFilterBySystem()).collect(toList());
        if (CollectionUtils.isEmpty(realTasks)) {
            log.info("无可用任务");
            return;
            // 先把服务启动起来，先不跑定时任务也可以，后续可能提供 web 数据查询服务
        }

        // 有可执行任务才需要初始化调度器
        initTaskScheduler();
        // 初始化每个任务
        realTasks.forEach(this::initTask);
    }

    @Override
    public void initTask(DataTask task) {
        String taskName = task.getTaskName();
        if (futureMap.containsKey(taskName)) {
            log.debug("任务已存在，不重复添加");
            return;
        }
        log.info("init task {} - {}", taskName, task);
        // 约定：处理任务的 bean 名称跟 taskName 保持一致
        IDataTask dataTask = applicationContext.getBean(taskName, IDataTask.class);
        if (Objects.isNull(dataTask)) {
            log.error("无可用任务处理器（spring bean）：{}", taskName);
            return;
        }
        CronTask cronTask = new CronTask(taskWrap(dataTask, task), task.getCron());
        ScheduledFuture<?> future = taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        futureMap.put(taskName, future);
    }

    @Override
    public ScheduledFuture<?> scheduledFuture(String taskName) {
        return futureMap.get(taskName);
    }

    @Override
    public Map<String, ScheduledFuture<?>> scheduledFuture() {
        return futureMap;
    }

    @Override
    public boolean removeTask(String taskName) {
        ScheduledFuture<?> future = futureMap.get(taskName);
        future.cancel(false);
        futureMap.remove(taskName);
        return true;
    }


}
