package config;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 15:35
 */
public class AbstractConfig {

    private GlobalConfig globalConfig;

    public void init(GlobalConfig globalConfig) {
        this.globalConfig=globalConfig;
    }

    public ApplicationConfig getApplicationConfig() {
        return globalConfig.getApplicationConfig();
    }

    public ClusterConfig getClusterConfig() {
        return globalConfig.getClusterConfig();
    }

    public ProtocolConfig getProtocolConfig() {
        return globalConfig.getProtocolConfig();
    }

    public RegistryConfig getRegistryConfig() {
        return globalConfig.getRegistryConfig();
    }


}
