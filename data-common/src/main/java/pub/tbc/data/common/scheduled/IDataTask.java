package pub.tbc.data.common.scheduled;

/**
 * 数据任务接口
 *
 * @Author tbc by 2020-10-25
 */
public interface IDataTask {
    /**
     * 执行数据任务
     *
     * @return 执行结果
     */
    TaskResult execute();
}
