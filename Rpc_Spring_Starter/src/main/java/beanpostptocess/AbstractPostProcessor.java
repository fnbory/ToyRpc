package beanpostptocess;

import config.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 15:28
 */
public class AbstractPostProcessor implements BeanPostProcessor {

    private GlobalConfig globalConfig;

    public void init(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        globalConfig=GlobalConfig.builder().clusterConfig(clusterConfig).applicationConfig(applicationConfig).registryConfig(registryConfig).protocolConfig(protocolConfig).build();
    }

    void initconfig(AbstractConfig config){
        config.init(globalConfig);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
