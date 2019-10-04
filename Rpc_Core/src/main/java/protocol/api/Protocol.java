package protocol.api;

import config.ServiceConfig;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 18:09
 */
public interface Protocol {




    void close();

    <T> void export(Invoker invoker, ServiceConfig<T> tServiceConfig);

    ServiceConfig referLocalService(String interfaceName);
}
