package Server.spring.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;

/**
 * @Author: fnbory
 * @Date: 2019/8/26 14:24
 */
@Getter
@Setter
public class PortBean implements FactoryBean<ProtocolPort>, InitializingBean, Serializable {

    private Integer port;

    private ProtocolPort protocolPort;

    public PortBean(){}

    public PortBean(Integer port){
        this.port=port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        protocolPort=new ProtocolPort();
        protocolPort.setPort(port);
    }

    @Override
    public ProtocolPort getObject() throws Exception {
        return protocolPort;
    }

    @Override
    public Class<?> getObjectType() {
        return ProtocolPort.class;
    }
}
