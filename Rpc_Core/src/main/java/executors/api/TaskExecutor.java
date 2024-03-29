package executors.api;

/**
 * @Author: fnbory
 * @Date: 2019/10/4 17:05
 */
public interface TaskExecutor {

    void init(Integer threads);

    void submit(Runnable runnable);

    void close();

}
