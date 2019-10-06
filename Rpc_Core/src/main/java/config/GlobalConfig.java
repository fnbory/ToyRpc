package config;

import cluster.LoadBalancer;
import executors.api.TaskExecutor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import protocol.api.Protocol;
import proxy.api.RpcProxyFactory;
import registry.ServiceRegistry;
import serialize.api.Serializer;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 16:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GlobalConfig {

    private ClusterConfig clusterConfig;

    private RegistryConfig registryConfig;

    private ApplicationConfig applicationConfig;

    private ProtocolConfig protocolConfig;

    public int getPort(){
        return protocolConfig.getPort();
    }

    public Serializer getSerializer() {
        return applicationConfig.getSerializerInstance();
    }

    public RpcProxyFactory getProxyFactory() {
        return applicationConfig.getProxyFactoryInstance();
    }


    public LoadBalancer getLoadBalancer() {
        return clusterConfig.getLoadBalancerInstance();
    }

    public TaskExecutor getServerExecutor(){
        return protocolConfig.getExecutor().getServer().getExecutorInstance();
    }

    public TaskExecutor getClientExecutor(){
        return protocolConfig.getExecutor().getClient().getExecutorInstance();
    }

    public Protocol getProtocol(){
        return protocolConfig.getProtocolInstance();
    }


    public ServiceRegistry getServiceRegistry() {
        return registryConfig.getServiceRegistry();
    }
}
