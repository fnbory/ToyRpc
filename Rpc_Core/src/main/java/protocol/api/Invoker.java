package protocol.api;

import common.domain.RpcResponse;
import registry.api.ServiceURL;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 16:07
 */
public interface Invoker<T> {

    Class<T> interfaceClass();

    String getInterfaceName();

    RpcResponse invoke(InvokeParam invokeParam);

    boolean isAvailable();

    ServiceURL getServiceURL();
}
