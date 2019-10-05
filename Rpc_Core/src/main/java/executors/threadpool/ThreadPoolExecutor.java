package executors.threadpool;

import executors.api.support.AbstractExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: fnbory
 * @Date: 2019/10/4 17:12
 */
public class ThreadPoolExecutor extends AbstractExecutor {

    private ExecutorService executorService;

    @Override
    public void init(Integer threads) {
        executorService=new java.util.concurrent.ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(),
                new ThreadFactory() {
                    private AtomicInteger atomicInteger=new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r,"threadpool-"+atomicInteger.getAndIncrement());
                    }
                },new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Override
    public void submit(Runnable runnable) {
        executorService.submit(runnable);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
