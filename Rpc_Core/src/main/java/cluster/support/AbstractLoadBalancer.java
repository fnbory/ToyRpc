package cluster.support;

import cluster.ClusterInvoker;
import cluster.LoadBalancer;
import common.domain.RpcRequest;
import config.GlobalConfig;
import config.ReferenceConfig;
import lombok.extern.slf4j.Slf4j;
import protocol.api.Invoker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: fnbory
 * @Date: 2019/10/2 18:09
 */
@Slf4j
public abstract class AbstractLoadBalancer implements LoadBalancer {

    private GlobalConfig globalConfig;

    private Map<String, ClusterInvoker> interfaceInvokers = new ConcurrentHashMap<>();

    public void updateGlobalConfig(GlobalConfig globalConfig) {
        if(this.globalConfig == null) {
            this.globalConfig = globalConfig;
        }else {
            if(globalConfig.getApplicationConfig() != null) {
                this.globalConfig.setApplicationConfig(globalConfig.getApplicationConfig());
            }
            if(globalConfig.getProtocolConfig() != null) {
                this.globalConfig.setProtocolConfig(globalConfig.getProtocolConfig());
            }
            if(globalConfig.getRegistryConfig() != null) {
                this.globalConfig.setRegistryConfig(globalConfig.getRegistryConfig());
            }
            if(globalConfig.getClusterConfig() != null) {
                this.globalConfig.setClusterConfig(globalConfig.getClusterConfig());
            }
        }
    }

    @Override
    public Invoker select(List<Invoker> invokers, RpcRequest request) {
        if (invokers.size() == 0) {
            log.info("select->不存在可用invoker，直接退出");
            return null;
        }
        // 调整endpoint，如果某个服务器不提供该服务了，则看它是否还提供其他服务，如果都不提供了，则关闭连接
        // 如果某个服务器还没有连接，则连接；如果已经连接，则复用
        Invoker invoker = doSelect(invokers, request);
        log.info("LoadBalance:{},chosen invoker:{},requestId:" + request.getRequestId(), this.getClass().getSimpleName(), invoker.getServiceURL());
        return invoker;
    }

    protected abstract Invoker doSelect(List<Invoker> invokers, RpcRequest request);

    @Override
    public <T> Invoker referCluster(ReferenceConfig<T> referenceConfig) {
        String interfaceName=referenceConfig.getInterfaceName();
        ClusterInvoker clusterInvoker;
        if(!interfaceInvokers.containsKey(interfaceName)){
            clusterInvoker=new ClusterInvoker(referenceConfig.getInterfaceClass(), interfaceName, globalConfig);
            interfaceInvokers.put(interfaceName, clusterInvoker);
            return clusterInvoker;
        }
        return interfaceInvokers.get(interfaceName);
    }
}
