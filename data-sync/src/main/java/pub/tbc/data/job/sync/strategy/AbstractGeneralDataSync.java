package pub.tbc.data.job.sync.strategy;

import pub.tbc.data.common.service.ConfigService;
import pub.tbc.data.job.sync.AbstractDataSyncTask;
import pub.tbc.data.job.sync.LongPrimaryProvider;
import pub.tbc.data.job.sync.MapperProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @Author tbc by 2020-10-25
 */
@Slf4j
public abstract class AbstractGeneralDataSync<S, T extends LongPrimaryProvider, P>
        extends AbstractDataSyncTask<S, T, P>
        implements MapperProvider<S, T, P> {

    // @formatter:off

    /** 同步位置提前量 */
    protected P positionLead(P position) {
        return position;
    }

    /** 真正获取数据的地方 */
    protected List<S> realGetSourceData(P position, int count) {
        return getSourceMapper().findByPositionLimit(position, count);
    }
    // @formatter:on

    @Inject
    private ConfigService configService;

    /**
     * 如果新的同步位置小于等于旧的同步位置，说明同步进度没有往前推进
     */
    private boolean noForward(List<S> data, P position, int count) {
        if (CollectionUtils.isEmpty(data)) {
            return false;
        }
        P newPosition = extractNewPosition(data);
        return compare(newPosition, position) <= 0 && data.size() == count;
    }

    /**
     * 获取数据：附加计时及日志
     *
     * @param position
     * @param count
     * @return
     */
    protected List<S> doGetSourceData(P position, int count) {
        StopWatch watch = new StopWatch("执行查询");
        watch.start();

        List<S> result = realGetSourceData(position, count);

        watch.stop();
        log.info("查询【{}}】耗时：{}，查询位置：{}, 查询数量：{}，实际查出数量：{}",
                getSyncType(),
                watch.getTotalTimeMillis(),
                position,
                count,
                result.size()
        );
        return result;
    }

    /**
     * 提供同步位提前量及同步数量自动扩容机制
     */
    @Override
    protected List<S> getSourceData(P position) {
        // 同步位置给个提前量，减小漏同步的概率
        P leadPosition = positionLead(position);
        int count = plainSyncCount();
        List<S> data = doGetSourceData(leadPosition, count);
        // 最多扩容次数
        int maxNum = configService.syncLimitExpandMax();
        int num = 0;
        // 如果同步进度没有往前推进，需要扩容，增加单次同步数量
        while (noForward(data, position, count)) {
            count += count;
            data = doGetSourceData(leadPosition, count);
            if (num++ >= maxNum) {
                log.error("单次获取数据量达到 {} 位置偏移量仍然没有往前推进，考虑数据有问题，人工检查处理", count);
                break;
            }
        }
        return data;
    }

    @Override
    protected boolean write(List<T> data) {
        return saveOrUpdate(data);
    }

    protected boolean saveOrUpdate(List<T> data) {
        // todo : insert on duplicate key update

        // 先拿出新获取的数据的 主键
        // 合约中可以是原表主键
        List<Long> dataIds = data.stream().map(this::primaryKey).collect(toList());
        // 去数据库中查找已存的ID，这些需要 update
        List<Long> updateIds = findInIds(dataIds);
        // 筛选出所有不存在的ID，这些需要 insert
        List<Long> insertIds = dataIds.stream().filter(id -> !updateIds.contains(id)).collect(toList());

        insert(data.stream().filter(t -> insertIds.contains(t.primary())).collect(toList()));
        if (this instanceof AbstractGeneralDataSyncByTime) {
            update(data.stream().filter(t -> updateIds.contains(t.primary())).collect(toList()));
        }
        return true;
    }

    protected Long primaryKey(T t) {
        return t.primary();
    }

    protected List<Long> findInIds(List<Long> dataIds) {
        return getTargetMapper().findInIds(dataIds);
    }

    protected boolean insert(List<T> data) {
        if (CollectionUtils.isEmpty(data)) {
            log.info("没有需要插入的数据");
            return false;
        }
        return getTargetMapper().batchInsert(data) > 0;
    }

    protected boolean update(List<T> data) {
        if (CollectionUtils.isEmpty(data)) {
            log.info("没有需要更新的数据");
            return false;
        }
        return getTargetMapper().batchUpdate(data) > 0;
    }
}
