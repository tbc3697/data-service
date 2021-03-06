package pub.tbc.data.job.sync;

import pub.tbc.data.job.mapper.SourceMapper;
import pub.tbc.data.job.mapper.TargetMapper;

/**
 * 同步相关基础 mapper 提供者
 *
 * @param <S> 待同步数据的类型
 * @param <T> 待同步数据处理后待写入的数据类型
 * @param <P> 同步位置的数据类型
 * @Author tbc by 2020-10-25
 */
public interface MapperProvider<S, T, P> {

    /**
     * 获取原始数据的 mapper
     *
     * @return
     */
    SourceMapper<S, P> getSourceMapper();

    /**
     * 目标库的 mapper
     *
     * @return
     */
    TargetMapper<T> getTargetMapper();
}
