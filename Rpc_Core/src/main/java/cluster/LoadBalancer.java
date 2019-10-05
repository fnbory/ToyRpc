package cluster;

import config.ReferenceConfig;
import protocol.api.Invoker;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 19:21
 */
public interface LoadBalancer {

    <T> Invoker referCluster(ReferenceConfig<T> tReferenceConfig);


}
