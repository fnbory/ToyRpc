package Server.zk;

import Server.Consumer;
import Server.Provider;

import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/8/27 17:20
 */
public interface Iregister {

    void registerService(List<Provider> providers);

    List<Provider> discover(String serviceName,String version);

    default void registerConsumer(Consumer consumer){}

    default void subscribe(String service){}

}
