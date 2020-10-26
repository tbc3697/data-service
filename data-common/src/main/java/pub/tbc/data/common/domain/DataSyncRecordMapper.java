package pub.tbc.data.common.domain;

import org.apache.ibatis.annotations.Param;

/**
 * @Author tbc by 2020-10-25
 */
public interface DataSyncRecordMapper {

    int insert(DataSyncRecord record);

    /**
     * 按同步类型获取同步记录
     *
     * @param type
     * @return
     */
    DataSyncRecord findByType(String type);

    /**
     * 更新同步位置
     *
     * @param recordId
     * @param position
     * @return
     */
    int updatePosition(@Param("recordId") int recordId, @Param("position") String position);

    /**
     * 重置所有同步位置
     */
    int resetPosition();


    int resetPositionByTaskName(String task);
}
