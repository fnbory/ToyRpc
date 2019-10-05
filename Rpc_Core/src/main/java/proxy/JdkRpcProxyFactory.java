package proxy;

import protocol.api.Invoker;
import proxy.api.support.AbstractProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author: fnbory
 * @Date: 2019/10/2 15:52
 */
public class JdkRpcProxyFactory extends AbstractProxyFactory {

    @Override
    protected <T> T doCreateProxy(Class interfaceClass, Invoker invoker) {
        return Proxy.newProxyInstance(
                invoker.interfaceClass().getClassLoader(),
                new Class[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return JdkRpcProxyFactory.this.invokeProxyMethod(invoker,method,args);
                    }
                }
        )
    }
}
