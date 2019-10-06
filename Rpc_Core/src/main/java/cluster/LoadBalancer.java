package cluster;

import common.domain.RpcRequest;
import config.ReferenceConfig;
import protocol.api.Invoker;

import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 19:21
 */
public interface LoadBalancer {

    <T> Invoker referCluster(ReferenceConfig<T> tReferenceConfig);

    Invoker select(List<Invoker> invokers, RpcRequest request);
}
