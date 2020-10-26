package pub.tbc.data.job.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pub.tbc.data.common.domain.DataSyncRecord;
import pub.tbc.data.common.domain.DataSyncRecordMapper;
import pub.tbc.data.common.scheduled.IDataTask;
import pub.tbc.data.common.scheduled.TaskResult;
import pub.tbc.data.common.service.ConfigService;
import pub.tbc.data.job.sync.extend.consumer.processor.DataConsumerProcessor;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * 抽象的同步任务，模板方法，定义了同步主流程
 *
 * @param <S> 源数据类型
 * @param <T> 处理后数据类型
 * @param <P> 同步位置类型
 * @Author tbc by 2020-10-25
 */
@Slf4j
public abstract class AbstractDataSyncTask<S, T, P> implements IDataTask, PositionHandler<S, P> {

    @Inject
    private DataSyncRecordMapper recordMapper;

    @Inject
    private ConfigService configService;

    @Inject
    private DataConsumerProcessor dataConsumerProcessor;

    private DataSyncRecord getSyncRecord() {
        DataSyncRecord record = recordMapper.findByType(getSyncType());
        if (record == null) {
            record = initRecord();
            recordMapper.insert(record);
        }
        return record;
    }

    private void updatePosition(DataSyncRecord record, P position) {
        recordMapper.updatePosition(record.getId(), convertToString(position));
    }

    /**
     * 数据同步过程
     */
    @Override
    public TaskResult execute() {
        // 0. 数据同步上下文
        DataSyncContext<S, T, P> syncContext = new DataSyncContext<>();
        syncContext.setPlainSyncCount(getSelf().plainSyncCount());

        // 1. 获取 position
        DataSyncRecord record = getSyncRecord();
        P position = extractCurrentPosition(record);
        syncContext.setBeforePosition(position);

        // 2. 查询源数据
        List<S> data = getSourceData(position);
        syncContext.setSources(data);
        if (CollectionUtils.isEmpty(data)) {
            return ret(syncContext);
        }
        dataConsumerProcessor.asyncProcessSourceConsumer(data);

        // 3. 提取新的 position
        P newPosition = extractNewPosition(data);
        if (newPosition == null) {
            newPosition = position;
        }
        syncContext.setAfterPosition(newPosition);

        // 4. 数据处理
        List<T> targetData = process(data);
        setContextTargets(syncContext, targetData);
        dataConsumerProcessor.asyncProcessSourceConsumer(targetData);
        dataConsumerProcessor.noTransactionProcessTargetConsumer(targetData);

        // 5. 写数据
        // 6. 记录新的 position
        getSelf().transactionWriteDataAndFlushPosition(targetData, record, newPosition);

        // 7. 返回
        return ret(syncContext);
    }

    /**
     * 以事务的方式执行数据写入和刷新 position
     *
     * @param targetData
     * @param record
     * @param newPosition
     */
    @Transactional(isolation = READ_COMMITTED, rollbackFor = Exception.class)
    protected void transactionWriteDataAndFlushPosition(List<T> targetData, DataSyncRecord record, P newPosition) {
        dataConsumerProcessor.transactionProcessTargetConsumer(targetData);

        // 5. 写数据
        write(targetData);

        // 6. 记录新的 position
        updatePosition(record, newPosition);
    }

    protected abstract AbstractDataSyncTask<S, T, P> getSelf();

    //
    // DataSyncContext 相关方法，子类按需重写
    /////////////////////////////////////////////////////////////////////

    protected DataSyncContext setContextTargets(DataSyncContext context, List<T> targetData) {
        context.setTargets(targetData);
        return context;
    }

    protected DataSyncContext buildDataSyncContext(List<S> data) {
        return new DataSyncContext()
                .setPlainSyncCount(plainSyncCount())
                .setRealSyncCount(data == null ? 0 : data.size())
                .setSources(data);
    }

    /**
     * 单次计划同步数据量
     *
     * @return
     */
    protected int plainSyncCount() {
        return configService.syncLimitCount();
    }


    //
    // 同步过程相关的方法，子类需要实现，或者使用默认实现
    /////////////////////////////////////////////////////////////////////

    /**
     * 对数据进行处理，默认原样同步的是不需要进行处理的
     *
     * @param sourceData
     * @return
     */
    protected List<T> process(List<S> sourceData) {
        return (List<T>) sourceData;
    }

    /**
     * 默认返回策略： 计划同步数量与实现同步数量相等可认为仍有待同步数据，就返回 true 表示继续处理
     *
     * @param context
     * @return
     */
    protected TaskResult ret(DataSyncContext context) {
        return new TaskResult().setContinueExec(
                context.getPlainSyncCount() <= context.getRealSyncCount()
        );
    }

    /**
     * 获取源数据
     *
     * @param position
     * @return
     */
    protected abstract List<S> getSourceData(P position);

    /**
     * 本地写数据
     *
     * @param data
     * @return
     */
    protected abstract boolean write(List<T> data);

    /**
     * 同步类型
     *
     * @return
     */
    protected abstract String getSyncType();

    protected abstract DataSyncRecord initRecord();


}
