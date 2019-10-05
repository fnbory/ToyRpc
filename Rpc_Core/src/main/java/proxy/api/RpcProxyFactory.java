package proxy.api;

import protocol.api.Invoker;

/**
 * @Author: fnbory
 * @Date: 2019/10/2 16:02
 */
public interface RpcProxyFactory {


    <T> Invoker getInvoker(T ref, Class<T> interfaceClass);


    <T> T createProxy(Invoker invoker);
}
