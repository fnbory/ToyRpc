package config;

import cluster.LoadBalancer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import proxy.api.RpcProxyFactory;
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

}
