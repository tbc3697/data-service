package pub.tbc.data.job.sync.strategy;


import pub.tbc.data.common.service.ConfigService;
import pub.tbc.data.common.domain.DataSyncRecord;
import pub.tbc.data.job.sync.LongPrimaryProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * 基于 主键 的数据同步策略类 <br>
 * <br>
 * 基于主键 ID 进行同步，仍然存在漏数据问题，比如长事务初期 insert 的记录，很容易被漏掉；<br>
 * 物理删除的数据仍然没办法检测；<br>
 *
 * @Author tbc by 2020-10-25
 */
@Slf4j
public abstract class AbstractGeneralDataSyncByPrimary<S, T extends LongPrimaryProvider> extends AbstractGeneralDataSync<S, T, Long> {
    /**
     * 默认同步开始位置
     */
    private final Long DEFAULT_BEGIN_POSITION = 1L;

    @Inject
    private ConfigService configService;

    protected Long extractNewPositionByFunction(List<S> data, Function<S, Long> func) {
        return data.stream().map(func).max(Comparator.naturalOrder()).orElse(null);
    }

    @Override
    public int compare(Long p1, Long p2) {
        if (p1 == null || p2 == null) {
            throw new RuntimeException("比较对象不能为空");
        }
        return Long.compare(p1, p2);
    }

    @Override
    protected Long positionLead(Long position) {
        return position - configService.leadPositionById();
    }

    @Override
    public Long extractCurrentPosition(DataSyncRecord record) {
        return covertFromString(record.getPosition());
    }

    @Override
    public Long covertFromString(String position) {
        if (StringUtils.isEmpty(position)) {
            return DEFAULT_BEGIN_POSITION;
        }
        return Long.valueOf(position);
    }

    @Override
    public String convertToString(Long l) {
        return String.valueOf(l);
    }

    @Override
    protected DataSyncRecord initRecord() {
        return new DataSyncRecord().setSyncType(getSyncType()).setPosition(convertToString(DEFAULT_BEGIN_POSITION));
    }
}
