package config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fnbory
 * @Date: 2019/10/4 16:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Executors {

    private ExecutorConfig server;

    private ExecutorConfig client;





    public void close() {
        if(client!=null){
            client.close();
        }
        if(server!=null){
            server.close();
        }
    }
}
