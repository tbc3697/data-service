package pub.tbc.data.common.executor;

/**
 * @Author tbc by 2020-10-25
 */
public class GlobalExecutorUtil {
    public static final String BEAN_NAME = "globalIoExecutor";

    static boolean enabledGlobalIoThreadPool = false;

    public static boolean enabledGlobalIoThreadPool() {
        return enabledGlobalIoThreadPool;
    }
}
