package cluster.support;

import cluster.ClusterInvoker;
import cluster.LoadBalancer;
import config.GlobalConfig;
import config.ReferenceConfig;
import protocol.api.Invoker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: fnbory
 * @Date: 2019/10/2 18:09
 */
public class AbstractLoadBalancer implements LoadBalancer {

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
