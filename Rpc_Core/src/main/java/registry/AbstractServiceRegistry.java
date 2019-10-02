package registry;

import config.RegistryConfig;
import lombok.Setter;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 14:50
 */
@Setter
public abstract class   AbstractServiceRegistry implements  ServiceRegistry {
    protected RegistryConfig registryConfig;

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }
}
