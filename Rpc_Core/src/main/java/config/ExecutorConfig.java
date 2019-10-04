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


    public void close() {
        executorInstance.close();
    }
}
