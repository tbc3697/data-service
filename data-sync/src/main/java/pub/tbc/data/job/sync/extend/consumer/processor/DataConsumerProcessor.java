package pub.tbc.data.job.sync.extend.consumer.processor;

import java.util.List;

/**
 * @Author tbc by 2020-10-25
 */
public interface DataConsumerProcessor {

    <T> void asyncProcessSourceConsumer(List<T> data);

    <T> void asyncProcessTargetConsumer(List<T> data);

    <T> void noTransactionProcessTargetConsumer(List<T> data);

    <T> void transactionProcessTargetConsumer(List<T> data);



}
