package proxy.api.support;

import protocol.api.InvokeParam;
import protocol.api.Invoker;
import protocol.api.support.AbstractInvoker;
import proxy.api.RpcProxyFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: fnbory
 * @Date: 2019/10/2 16:05
 */
public class AbstractProxyFactory implements RpcProxyFactory {

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> interfaceClass) {
        return new AbstractInvoker<T>(){
            @Override
            public Class<T> interfaceClass() {
                return interfaceClass;
            }

            @Override
            public String getInterfaceName() {
                return interfaceClass.getName();
            }

            @Override
            public RpcResponse invoke(InvokeParam invokeParam) {
                try {
                    Method method=proxy.getClass().getMethod(invokeParam.getMethodName(),invokeParam.getParameterTypes());
                    method.invoke(proxy,invokeParam.getParameters());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
