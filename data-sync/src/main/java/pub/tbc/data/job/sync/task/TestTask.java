package pub.tbc.data.job.sync.task;

import pub.tbc.data.common.scheduled.IDataTask;
import pub.tbc.data.common.scheduled.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author tbc by 2020-10-25
 */
@Service
@Slf4j
public class TestTask implements IDataTask {
    @Override
    public TaskResult execute() {
        log.info("task execute");
        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new TaskResult().setContinueExec(false);
    }
}
