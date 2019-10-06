package protocol.api.support;

import common.domain.RpcResponse;
import config.GlobalConfig;
import protocol.api.InvokeParam;
import protocol.api.Invoker;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 16:19
 */
public class   AbstractInvoker<T> implements Invoker {

    private Class interfaceClass;

    private String interfaceName;

    private GlobalConfig globalConfig;


    @Override
    public Class interfaceClass() {
        return interfaceClass;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public RpcResponse invoke(InvokeParam invokeParam) {

        }
}
