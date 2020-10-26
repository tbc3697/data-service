package pub.tbc.data.job.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 同步目标表的共通操作
 *
 * @author tbc by 2020-10-25
 */
public interface TargetMapper<T> {

    /**
     * 查询待同步数据中已存在的ID
     *
     * @param ids 匹配的ID
     * @return 已存在的ID
     */
    List<Long> findInIds(List<Long> ids);

    /**
     * 批量插入
     *
     * @param data 插入数据
     * @return 影响行数
     */
    int batchInsert(List<T> data);

    /**
     * 批量修改
     *
     * @param data 修改数据
     * @return 影响行数
     */
    int batchUpdate(List<T> data);

    /**
     * 如果不存在插入, 已存在修改
     *
     * @param data 插入数据
     * @return 影响行数
     */
    int batchInsertOnDuplicateKey(@Param("records") List<T> data);

    /**
     * 根据条件删除数据
     *
     * @param param 条件
     * @return 影响行数
     */
    int deleteAll(Map<String, Object> param);

}
