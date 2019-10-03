package protocol.api;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 16:07
 */
public interface Invoker<T> {

    Class<T> interfaceClass();

    String getInterfaceName();

    RpcResponse invoke(InvokeParam invokeParam);


}
