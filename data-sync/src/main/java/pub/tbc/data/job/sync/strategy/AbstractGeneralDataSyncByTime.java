package pub.tbc.data.job.sync.strategy;

import pub.tbc.data.common.service.ConfigService;
import pub.tbc.data.common.domain.DataSyncRecord;
import pub.tbc.data.job.sync.LongPrimaryProvider;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static java.time.LocalDateTime.of;

/**
 * @Author tbc by 2020-10-25
 */
public abstract class AbstractGeneralDataSyncByTime<S, T extends LongPrimaryProvider> extends AbstractGeneralDataSync<S, T, LocalDateTime> {
    /**
     * 默认同步开始时间: unix 时间戳起始时间
     */
    private final LocalDateTime DEFAULT_BEGIN_POSITION = of(
            LocalDate.of(1970, 1, 1),
            LocalTime.of(0, 0, 0, 000)
    );

    /**
     * 精确到纳秒的标准日期格式 formatter
     */
    private DateTimeFormatter STANDARD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    @Inject
    private ConfigService configService;


    protected LocalDateTime extractNewPositionByFunction(List<S> data, Function<S, LocalDateTime> func) {
        return data.stream().map(func).max(Comparator.naturalOrder()).orElse(null);
    }

    @Override
    public int compare(LocalDateTime p1, LocalDateTime p2) {
        return p1.compareTo(p2);
    }

    @Override
    protected LocalDateTime positionLead(LocalDateTime position) {
        return position.minusSeconds(configService.leadPositionByTime());
    }

    @Override
    public LocalDateTime extractCurrentPosition(DataSyncRecord record) {
        return covertFromString(record.getPosition());
    }

    @Override
    public LocalDateTime covertFromString(String position) {
        if (StringUtils.isEmpty(position)) {
            return DEFAULT_BEGIN_POSITION;
        }
        return LocalDateTime.from(STANDARD_FORMATTER.parse(position));
    }

    @Override
    public String convertToString(LocalDateTime localDateTime) {
        return localDateTime.format(STANDARD_FORMATTER);
    }

    @Override
    protected DataSyncRecord initRecord() {
        return new DataSyncRecord().setSyncType(getSyncType()).setPosition(convertToString(DEFAULT_BEGIN_POSITION));
    }
}
