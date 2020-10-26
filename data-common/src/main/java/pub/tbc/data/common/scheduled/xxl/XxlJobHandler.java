package pub.tbc.data.common.scheduled.xxl;

import pub.tbc.data.common.scheduled.IDataTask;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StopWatch;

import javax.inject.Inject;

/**
 * 适配 XXL-JOB
 *
 * @Author tbc by 2020-10-25
 */
@Slf4j
public class XxlJobHandler extends IJobHandler {

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public ReturnT<String> execute(String springBeanName) throws Exception {
        IDataTask dataTask = applicationContext.getBean(springBeanName, IDataTask.class);
        if (dataTask == null) {
            String msg = "未找到可用任务处理器";
            log.error(msg);
            ReturnT<String> result = ReturnT.FAIL;
            result.setMsg(msg);
            return result;
        }

        log.info("开始执行任务：{}", springBeanName);
        StopWatch stopWatch = new StopWatch(springBeanName);
        dataTask.execute();
        log.info(stopWatch.shortSummary());
        return ReturnT.SUCCESS;
    }
}
