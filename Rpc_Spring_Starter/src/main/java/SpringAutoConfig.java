import beanpostptocess.ProviderPostProcess;
import cluster.LoadBalancer;
import cluster.support.AbstractLoadBalancer;
import common.ExtentionLoader;
import common.enumeration.*;
import common.exception.Rpcexception;
import config.*;
import executors.api.TaskExecutor;
import executors.api.support.AbstractExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import protocol.api.support.AbstractProtocol;
import proxy.api.RpcProxyFactory;
import registry.impl.ZkRegistry;
import serialize.api.Serializer;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 20:18
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(RpcProeprties.class)
public class SpringAutoConfig implements InitializingBean {

    @Autowired
    RpcProeprties proeprties;

  //  @Autowire  原则上不采用Spring来getBean，太重，使用者可能会导致版本冲突
    ExtentionLoader extentionloader;

    @Bean(initMethod = "init",destroyMethod = "close")
    public RegistryConfig registryConfig(){
        RegistryConfig registryConfig=proeprties.getRegistryConfig();

        if(registryConfig==null){
            throw new Rpcexception(ERROR_ENUM.APP_CONFIG_FILE_ERROR, "必须配置registry");
        }

        if(ProtocolType.INJVM!=ProtocolType.valueOf(proeprties.getProtocolConfig().getType().toUpperCase())){
            ZkRegistry zkRegistry=new ZkRegistry(registryConfig);
            registryConfig.setServiceRegistry(zkRegistry);
        }
        // injvm
        log.info("{}",registryConfig);
        return registryConfig;
    }

    @Bean
    public ApplicationConfig applicationConfig(){
        ApplicationConfig applicationConfig=proeprties.getApplicationConfig();
        if (applicationConfig == null) {
            throw new Rpcexception(ERROR_ENUM.APP_CONFIG_FILE_ERROR, "必须配置applicationConfig");
        }
        applicationConfig.setProxyFactoryInstance(extentionloader.load(ProxyFactoryType.class,
                applicationConfig.getProxy(), RpcProxyFactory.class));
        applicationConfig.setSerializerInstance(extentionloader.load(SerializerType.class,
                applicationConfig.getSerialize(), Serializer.class));
        log.info("{}", applicationConfig);
        return applicationConfig;
    }

    @Bean
    public ClusterConfig clusterConfig(RegistryConfig registryConfig,ApplicationConfig applicationConfig){
        ClusterConfig clusterConfig=proeprties.getClusterConfig();
        if (clusterConfig == null) {
            throw new Rpcexception(ERROR_ENUM.APP_CONFIG_FILE_ERROR, "必须配置clusterConfig");
        }
        AbstractLoadBalancer loadBalancer=(AbstractLoadBalancer) extentionloader.load(LoadBalanceType.class,
                clusterConfig.getLoadBalancer(), LoadBalancer.class);

        loadBalancer.updateGlobalConfig(GlobalConfig.builder()
                .applicationConfig(applicationConfig)
                .registryConfig(registryConfig)
                .clusterConfig(clusterConfig).build());
        // 设置容错机制
        clusterConfig.setLoadBalancerInstance(loadBalancer);
        log.info("{}", clusterConfig);

        return clusterConfig;
    }

    @Bean(destroyMethod = "close")
    public ProtocolConfig protocolConfig(ApplicationConfig applicationConfig,RegistryConfig registryConfig,ClusterConfig clusterConfig){
        ProtocolConfig protocolConfig=proeprties.getProtocolConfig();
        if (protocolConfig == null) {
            throw new Rpcexception(ERROR_ENUM.APP_CONFIG_FILE_ERROR, "必须配置protocolConfig");
        }
        AbstractProtocol protocol=extentionloader.load(ProtocolType.class,protocolConfig.getType(), AbstractProtocol.class);

        protocol.init(GlobalConfig.builder()
                    .clusterConfig(clusterConfig)
                    .registryConfig(registryConfig)
                    .applicationConfig(applicationConfig)
                    .protocolConfig(protocolConfig)
                    .build());

        protocolConfig.setProtocolInstance(protocol);
        ((AbstractLoadBalancer)clusterConfig.getLoadBalancerInstance()).updateGlobalConfig(GlobalConfig.builder()
                        .protocolConfig(protocolConfig)
                        .build());

        Executors executors=protocolConfig.getExecutor();
        if(executors!=null){
            ExecutorConfig serverExecutor=executors.getServer();
            if(serverExecutor!=null){
                TaskExecutor executor=extentionloader.load(ExecutorType.class,serverExecutor.getType(), AbstractExecutor.class);
                executor.init(serverExecutor.getThreads());
                serverExecutor.setExecutorInstance(executor);
            }
            ExecutorConfig clientExecutor=executors.getClient();
            if(clientExecutor!=null){
                TaskExecutor executor=extentionloader.load(ExecutorType.class,clientExecutor.getType(),AbstractExecutor.class);
                executor.init(clientExecutor.getThreads());
                clientExecutor.setExecutorInstance(executor);
            }
        }
        log.info("{}", protocolConfig);
        return protocolConfig;

    }

    @Bean
    public ProviderPostProcess providerPostProcess(ApplicationConfig applicationConfig, ClusterConfig clusterConfig,
                                                            ProtocolConfig protocolConfig, RegistryConfig registryConfig){
        ProviderPostProcess providerPostProcess=new ProviderPostProcess();
        providerPostProcess.init(applicationConfig,clusterConfig,protocolConfig,registryConfig);
        log.info("ProviderPostProcessor init");
        return providerPostProcess;
    }




    @Override
    public void afterPropertiesSet() throws Exception {
        extentionloader= ExtentionLoader.getInstance();
        extentionloader.loadResurce();
    }
}
