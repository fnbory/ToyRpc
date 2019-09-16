package Server;

import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory;

/**
 * @Author: fnbory
 * @Date: 2019/8/23 15:27
 */
public class MyThredFactory extends DefaultManagedAwareThreadFactory {

    public MyThredFactory(){
        this.setDaemon(true);
        this.setThreadNamePrefix("toy-rpc-");
    }

    public MyThredFactory(String name){
        this.setDaemon(true);
        this.setThreadNamePrefix(name);
    }
}
