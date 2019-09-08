package Server.spring.bean;

import Server.Provider;
import Server.netty.request.NettyServer;
import Server.netty.request.Server;
import Server.spring.serialization.Iserialization;
import Server.spring.serialization.SerializationFactory;
import Server.utils.NetUtils;
import Server.zk.Iregister;
import Server.zk.ZkRegister;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/8/26 16:39
 */
public class ServiceBean  implements InitializingBean, ApplicationContextAware, Serializable {
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String interfaceName;
    @Getter
    @Setter
    private String version;
    @Getter
    @Setter
    private Integer weight;
    @Getter
    @Setter
    private Iregister register;
    @Getter
    @Setter
    private String serialization;
    @Getter
    @Setter
    private Object impl;

    @Getter
    private ApplicationContext applicationContext;
    @Getter
    @Setter
    private ProtocolPort protocolPort;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.build();
    }

    private void build() {
        if(register==null){
            register=applicationContext.getBean(ZkRegister.class);
        }
        if(this.protocolPort==null){
            protocolPort=applicationContext.getBean(ProtocolPort.class);
        }
        Iserialization serialization= SerializationFactory.resolve(this.serialization,this.interfaceName);
        List<Provider> providerList=new ArrayList<>(16);
        Provider provider=new Provider();
        provider.setSerialization(serialization.toString());
        provider.setServiceName(interfaceName);
        provider.setHost(NetUtils.getLocalHost());
        provider.setPort(protocolPort.getPort());
        if(weight==null){
            provider.setWeight(1);
        }
        else {
            provider.setWeight(weight);
        }
        provider.setVersion(version);
        providerList.add(provider);
        register.registerService(providerList);
        Server server = new NettyServer(serialization, this.applicationContext);
        server.bind(this.protocolPort.getPort());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }


}
