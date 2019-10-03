package config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import protocol.api.Invoker;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 14:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceConfig<T> extends AbstractConfig{

    private String interfaceName;

    private Class<T> interfaceClass;

    private  T ref;
    public void export() {
        Invoker invoker=getApplicationConfig().getProxyFactoryInstance().getInvoker(ref,interfaceClass);
        getProtocolConfig().getProtocolInstance().export(invoker,this);
    }
}
