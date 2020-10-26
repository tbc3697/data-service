package pub.tbc.data.common.domain;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tbc by 2020-10-25
 */
public interface DataTaskMapper {

    int insert(DataTask task);

    int update(DataTask task);

    int execTaskUpdateStatus(@Param("taskId") int taskId, @Param("workId") int workId);

    int finishTaskUpdateStatus(int taskId);

    List<DataTask> findAll();

    List<DataTask> findEnableTasks();

    List<DataTask> findList(@Param("taskStatus") Integer taskStatus);

    DataTask findById(Integer taskId);

    DataTask findByName(String  taskName);

    int enable(int taskId);

    int disable(int taskId);

    int clear();

}
