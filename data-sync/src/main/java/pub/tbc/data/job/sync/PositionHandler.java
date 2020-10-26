package pub.tbc.data.job.sync;


import pub.tbc.data.common.domain.DataSyncRecord;

import java.util.List;

/**
 * 同步位置处理器
 *
 * @param <S> 得到的原始数据类型
 * @param <P> 同步位的数据类型
 * @Author tbc by 2020-10-25
 */
public interface PositionHandler<S, P> {
    /**
     * 提取原同步位置
     *
     * @param record
     * @return
     */
    P extractCurrentPosition(DataSyncRecord record);

    /**
     * 提取新的同步位置
     *
     * @param sourceData
     * @return
     */
    P extractNewPosition(List<S> sourceData);

    /**
     * 从字符串转换为对应类型
     *
     * @param position
     * @return
     */
    P covertFromString(String position);

    /**
     * 转换为 String 型，DataSyncRecord 中存储 String 值
     *
     * @param p
     * @return
     */
    String convertToString(P p);

    /**
     * 同步位置比较
     *
     * @param p1
     * @param p2
     * @return
     */
    int compare(P p1, P p2);
}
