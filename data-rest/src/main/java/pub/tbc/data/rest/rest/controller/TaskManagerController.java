package pub.tbc.data.rest.rest.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.*;
import pub.tbc.data.common.domain.DataTask;
import pub.tbc.data.common.domain.DataTaskMapper;
import pub.tbc.data.common.scheduled.custom.manager.DefaultTaskManager;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * @Author tbc by 2020/7/18 2:38 下午
 */
@RestController
@RequestMapping("v1/inner/task")
public class TaskManagerController {

    @Inject
    private DataTaskMapper taskMapper;

    @Inject
    private DefaultTaskManager taskManager;

    @Inject
    private ApplicationContext context;


    @GetMapping("/list")
    public List<DataTask> list(Integer taskStatus) {
        Map<String, ScheduledFuture<?>> futureMap = taskManager.scheduledFuture();
        List<DataTask> list = taskMapper.findList(taskStatus);
        if (futureMap != null && list != null) {
            return list.stream().filter(dt -> futureMap.containsKey(dt.getTaskName())).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 创建新任务（确保要创建的任务相关执行逻辑已编程完毕，且任务名称 = bean名称）
     *
     * @param task   要创建的任务
     * @param isInit 是否初始化（开始接受调度执行）
     * @return
     */
    @PostMapping
    public DataTask add(@RequestBody DataTask task, boolean isInit) {
        int inserted = taskMapper.insert(task);
        // 初始化刚创建的任务，开始调度
        if (isInit) {
            taskManager.initTask(task);
        }
        return task;
    }

    @PutMapping
    public Object edit(DataTask task) {
        return taskMapper.update(task);
    }


    @PutMapping("enable/{taskId}")
    public Object enable(@PathVariable("taskId") Integer taskId) {
        // status  0 -> 1
        return taskMapper.execTaskUpdateStatus(taskId, taskManager.getWorkId());
    }

    @PutMapping("disable/{taskId}")
    public Object disable(@PathVariable("taskId") Integer taskId) {
        if (taskId == null || taskId == 0) {
            return "taskId 错误";
        }
        if (taskMapper.disable(taskId) == 0) {
            return "任务执行中，不支持禁用";
        }
        return true;
    }

    @PutMapping("task/reloadAll")
    public Object reloadTaskFromDb() {
        List<DataTask> allTask = taskMapper.findAll();
        taskManager.scheduledFuture().values().forEach(s -> s.cancel(false));
        allTask.forEach(taskManager::initTask);
        return true;
    }

    @PutMapping("task/reload/{taskId}")
    public Object reloadTask(@PathVariable("taskId") Integer taskId) {
        DataTask task = taskMapper.findById(taskId);
        if (task == null) {
            return "任务不存在";
        }
        if (taskManager.removeTask(task.getTaskName())) {
            taskManager.initTask(task);
            return true;
        }
        return false;
    }

    @PutMapping("remove/{taskName}")
    public Object remove(@PathVariable("taskName") String taskName) {
        DataTask task = taskMapper.findByName(taskName);
        if (task == null) {
            return "任务不存在";
        }
        Object disableResult = disable(task.getId());

        boolean isOk = false;
        if (disableResult instanceof Boolean && (Boolean) disableResult) {
            isOk = taskManager.removeTask(taskName);
        }
        return "移除状态：" + isOk;
    }

    @PutMapping("remove/id/{taskId}")
    public Object remove(@PathVariable("taskId") Integer taskId) {
        DataTask task = taskMapper.findById(taskId);
        if (task == null) {
            return "任务不存在";
        }
        Object disableResult = disable(task.getId());

        boolean isOk = false;
        if (disableResult instanceof Boolean && (Boolean) disableResult) {
            isOk = taskManager.removeTask(task.getTaskName());
        }
        return "移除状态：" + isOk;
    }

    /**
     * 编辑并且重新加载任务
     *
     * @param task
     * @return
     */
    @PutMapping("edit/reload")
    public Object editAndReload(DataTask task) {
        Object edit = edit(task);
        if (edit instanceof Integer && ((Integer) edit) > 0) {
            return reloadTask(task.getId());
        }
        return false;
    }

    @PutMapping("stopAutoFlush")
    public Object stopAutoFlush() {
        taskManager.disableTaskAutoFlush();
        return true;
    }

    @PutMapping("startAutoFlush")
    public Object startAutoFlush() {
        taskManager.enableTaskAutoFlush((ConfigurableApplicationContext) context);
        return true;
    }


}
