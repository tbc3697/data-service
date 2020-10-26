package pub.tbc.data.common.scheduled;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author tbc by 2020-10-25
 */
@Data
@Accessors(chain = true)
public class TaskResult {
    private String taskName;
    /**
     * 任务执行是否成功
     */
    private boolean isOk = true;
    /**
     * 是否继续执行
     */
    private boolean continueExec = false;

    Object extObject;
}
