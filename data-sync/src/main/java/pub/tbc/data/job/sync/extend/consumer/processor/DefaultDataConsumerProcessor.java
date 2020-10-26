package pub.tbc.data.job.sync.extend.consumer.processor;

import com.alibaba.fastjson.JSON;
import org.springframework.util.CollectionUtils;
import pub.tbc.data.common.executor.GlobalExecutorUtil;
import pub.tbc.data.job.sync.extend.consumer.*;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static pub.tbc.data.common.executor.GlobalExecutorUtil.BEAN_NAME;

/**
 * @Author tbc by 2020-10-25
 */
@Service
public class DefaultDataConsumerProcessor implements DataConsumerProcessor {

    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private ExecutorService executorService;

    @Inject
    private ApplicationContext context;

    private ExecutorService buildExecutor() {
        ThreadGroup tGroup = Thread.currentThread().getThreadGroup();
        Supplier<String> tNameSup = () -> "data-consumer:" + threadNumber.getAndIncrement();
        return new ThreadPoolExecutor(
                10,
                10,
                0,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(10_000),
                runnable -> new Thread(tGroup, runnable, tNameSup.get(), 0)
        );
    }

    private ExecutorService initExecutor() {
        if (executorService == null) {
            executorService = GlobalExecutorUtil.enabledGlobalIoThreadPool()
                    ? context.getBean(BEAN_NAME, ThreadPoolExecutor.class)
                    : buildExecutor();
        }
        return executorService;
    }

    private Collection<? extends DataConsumer> getConsumers(Class<? extends DataConsumer> c) {
        Map<String, ? extends DataConsumer> beans = context.getBeansOfType(c);
        return beans.values();
    }

    private <T> List toList(String jsonStr, Class<T> tClass) {
        return JSON.parseArray(jsonStr, tClass);
    }

    private <T> Class<T> getClass(List<T> data) {
        T t = data.get(0);
        Class<T> c = (Class<T>) t.getClass();
        return c;
    }

    private ExecutorService getExecutor() {
        if (executorService == null) {
            synchronized (getClass()) {
                executorService = initExecutor();
            }
        }
        return executorService;
    }

    private <T> void process(List<T> data, Class<? extends DataConsumer> c, boolean isAsync) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        String jsonStr = JSON.toJSONString(data);
        Class<T> tClass = getClass(data);

        Collection<? extends DataConsumer> consumers = getConsumers(c);
        for (DataConsumer consumer : consumers) {
            List<T> cpData = toList(jsonStr, tClass);
            Runnable runnable = () -> consumer.accept(cpData);
            if (isAsync) {
                ExecutorService executor = getExecutor();
                executor.execute(runnable);
            } else {
                runnable.run();
            }
        }
    }

    @Override
    public <T> void asyncProcessSourceConsumer(List<T> data) {
        process(data, AsyncSourceDataConsumer.class, true);
    }

    @Override
    public <T> void asyncProcessTargetConsumer(List<T> data) {
        process(data, AsyncTargetDataConsumer.class, true);
    }

    @Override
    public <T> void noTransactionProcessTargetConsumer(List<T> data) {
        process(data, NoTransactionTargetDataConsumer.class, false);
    }

    @Override
    public <T> void transactionProcessTargetConsumer(List<T> data) {
        process(data, TransactionTargetDataConsumer.class, false);
    }

}
