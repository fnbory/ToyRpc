package common.enumeration;

import executors.api.TaskExecutor;
import executors.threadpool.ThreadPoolExecutor;

/**
 * @Author: fnbory
 * @Date: 2019/10/5 20:25
 */
public enum  ExecutorType implements ExtentionBaseType {

    THREADPOOL(new ThreadPoolExecutor()),;
    private TaskExecutor executor;

    ExecutorType(TaskExecutor executor){
        this.executor=executor;
    }

    @Override
    public Object getInstance() {
        return executor;
    }
}
