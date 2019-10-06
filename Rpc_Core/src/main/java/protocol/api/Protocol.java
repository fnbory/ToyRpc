package protocol.api;

import config.ReferenceConfig;
import config.ServiceConfig;
import registry.api.ServiceURL;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 18:09
 */
public interface Protocol {




    void close();

    <T> void export(Invoker invoker, ServiceConfig<T> tServiceConfig);

    ServiceConfig referLocalService(String interfaceName);

    Invoker refer(ReferenceConfig referenceConfig, ServiceURL serviceURL);
}
