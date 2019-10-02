package cluster.support;

import cluster.LoadBalancer;
import config.GlobalConfig;

/**
 * @Author: fnbory
 * @Date: 2019/10/2 18:09
 */
public class AbstractLoadBalancer implements LoadBalancer {

    private GlobalConfig globalConfig;

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
}
