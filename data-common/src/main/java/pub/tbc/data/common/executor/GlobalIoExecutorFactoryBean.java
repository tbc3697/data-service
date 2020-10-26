package pub.tbc.data.common.executor;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局线程池，配置线程数远多于 CPU 核数，一般专用于执行 IO 任务
 *
 * @Author tbc by 2020-10-25
 */
public class GlobalIoExecutorFactoryBean implements FactoryBean<ExecutorService> {

    @Value("${data.globalExecutor.corePoolSize:50}")
    private int corePoolSize;

    @Value("${data.globalExecutor.maximumPoolSize:200}")
    private int maximumPoolSize;

    @Value("${data.globalExecutor.keepAliveTime:0}")
    private long keepAliveTime;
    private TimeUnit keepAliveTimeUnit = TimeUnit.MILLISECONDS;

    /**
     * 阻塞队列最大容量
     */
    @Value("${data.globalExecutor.blockingQueueCapacity:100000}")
    private int blockingQueueCapacity;
    /**
     * 阻塞队列
     */
    private BlockingQueue<Runnable> queue;

    /**
     * 线程工厂，主要是定义线程名称前缀和线程组
     */
    private ThreadFactory threadFactory = new ThreadFactory() {
        // private final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "GlobalIoThread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    };

    private ThreadPoolExecutor threadPoolExecutor;

    private Object INIT_MONITOR_LOCK = new Object();

    private ThreadPoolExecutor initThreadPoolExecutor() {
        synchronized (INIT_MONITOR_LOCK) {
            if (threadPoolExecutor == null) {
                queue = new LinkedBlockingDeque<>(blockingQueueCapacity);
                threadPoolExecutor = new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        keepAliveTime,
                        keepAliveTimeUnit,
                        queue,
                        threadFactory
                );
            }
        }
        return threadPoolExecutor;
    }

    @Override
    public ExecutorService getObject() {
        return threadPoolExecutor == null ? initThreadPoolExecutor() : threadPoolExecutor;
    }

    @Override
    public Class<?> getObjectType() {
        return ExecutorService.class;
    }
}
