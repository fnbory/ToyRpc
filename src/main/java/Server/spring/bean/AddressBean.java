package Server.spring.bean;

import Server.zk.ZkRegister;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;

/**
 * @Author: fnbory
 * @Date: 2019/8/26 14:40
 */
@Getter
@Setter
public class AddressBean  implements FactoryBean<ZkRegister>, InitializingBean, Serializable {


    private String id;

    private ZkRegister zkRegister;

    private Integer session;

    private Integer connection;

    private String address;

    @Override
    public ZkRegister getObject() throws Exception {
        return this.zkRegister;
    }

    @Override
    public Class<?> getObjectType() {
        return ZkRegister.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.zkRegister=build();
    }

    private ZkRegister build() {
        if(session!=null&&connection!=null){
            ZkRegister zkRegister=new ZkRegister(address,session,connection);
        }
        else {
            zkRegister=new ZkRegister(address);
        }
        return zkRegister;
    }
}
