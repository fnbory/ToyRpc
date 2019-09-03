package Server.spring.bean;

import Server.zk.Iregister;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;

/**
 * @Author: fnbory
 * @Date: 2019/8/26 16:39
 */
public class ServiceBean  implements InitializingBean, ApplicationContextAware, Serializable {
    private String id;
    private String interfaceName;
    private String version;
    private Integer weight;
    private Iregister register;
    private String serialization;
    private Object impl;

    private ApplicationContext applicationContext;
    private ProtocolPort protocolPort;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.build();
    }

    private void build() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
