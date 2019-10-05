package cluster;

import common.domain.RpcResponse;
import config.GlobalConfig;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import protocol.api.InvokeParam;
import protocol.api.Invoker;

/**
 * @Author: fnbory
 * @Date: 2019/10/5 14:45
 */
@Slf4j
public class ClusterInvoker<T> implements Invoker<T> {

    private Class<T> interfaceClass;

    private String interfaceName;

    private GlobalConfig globalConfig;

    public ClusterInvoker(Class<T> interfaceClass, String interfaceName, GlobalConfig globalConfig) {
        this.interfaceClass = interfaceClass;
        this.interfaceName = interfaceName;
        this.globalConfig = globalConfig;
        init();
    }

    private void init() {
        if (globalConfig.getProtocol() instanceof InJvmProtocol) {
            addOrUpdate(ServiceURL.DEFAULT_SERVICE_URL);
        } else {
            globalConfig.getServiceRegistry().discover(interfaceName, (newServiceURLs -> {
                removeNotExisted(newServiceURLs);
            }), (serviceURL -> {
                addOrUpdate(serviceURL);
            }));
        }
    }

    @Override
    public Class<T> interfaceClass() {
        return interfaceClass;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public RpcResponse invoke(InvokeParam invokeParam) {
        return null;
    }
}
