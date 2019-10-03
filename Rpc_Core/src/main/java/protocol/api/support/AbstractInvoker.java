package protocol.api.support;

import protocol.api.InvokeParam;
import protocol.api.Invoker;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 16:19
 */
public class AbstractInvoker<T> implements Invoker {

    @Override
    public Class interfaceClass() {
        return null;
    }

    @Override
    public String getInterfaceName() {
        return null;
    }

    @Override
    public RpcResponse invoke(InvokeParam invokeParam) {
        return null;
    }
}
