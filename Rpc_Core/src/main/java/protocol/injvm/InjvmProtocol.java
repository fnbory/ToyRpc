package protocol.injvm;

import config.ReferenceConfig;
import config.ServiceConfig;
import protocol.api.Invoker;
import protocol.api.support.AbstractProtocol;
import registry.api.ServiceURL;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 19:26
 */
public class InjvmProtocol extends AbstractProtocol {

    @Override
    public <T> void export(Invoker invoker, ServiceConfig<T> tServiceConfig) {

    }

    @Override
    public Invoker refer(ReferenceConfig referenceConfig, ServiceURL serviceURL) {

    }
}
