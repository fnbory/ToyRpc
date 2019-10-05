package config;

import executors.api.TaskExecutor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fnbory
 * @Date: 2019/10/4 16:59
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorConfig {

    private TaskExecutor executorInstance;

    private String type;

    private Integer threads;

    private static final Integer DEFAULT_THREADS=Runtime.getRuntime().availableProcessors();

    public int getThreads(){
        if(threads!=null){
            return threads;
        }
        return DEFAULT_THREADS;
    }

    public void close() {
        executorInstance.close();
    }
}
