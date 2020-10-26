package pub.tbc.data.job.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author tbc by 2020-10-25
 */
public interface SourceMapper<S, P> {
    /**
     * 按同步位置查询指定数量的数据
     *
     * @param position
     * @param count
     * @return
     */
    List<S> findByPositionLimit(@Param("position") P position, @Param("count") int count);

    List<S> findByPositionLimitForTable(@Param("position") P position, @Param("count") int count, @Param("tableName") String tableName);

    List<S> findByPositionExcludeUsersLimitForTable(
            @Param("position") P position,
            @Param("count") int count,
            @Param("tableName") String tableName,
            @Param("uidList") List<Long> uidList
    );

    List<S> findAll();
}
