package executors.threadpool;

import executors.api.support.AbstractExecutor;

import java.util.concurrent.ExecutorService;

/**
 * @Author: fnbory
 * @Date: 2019/10/4 17:12
 */
public class ThreadPoolExecutor extends AbstractExecutor {

    private ExecutorService executorService;

    @Override
    public void submit(Runnable runnable) {
        executorService.submit(runnable);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
