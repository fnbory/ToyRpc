package registry;

import registry.api.ServiceURLAddOrUpdateCallback;
import registry.api.ServiceURLRemovalCallback;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 19:42
 */
public interface ServiceRegistry {


    void init();

    void discover(String interfaceName, ServiceURLRemovalCallback callback, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback);

    void registry(String address,String interfaceName);

    void close();
}
