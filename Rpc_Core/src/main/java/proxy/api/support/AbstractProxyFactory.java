package proxy.api.support;

import common.domain.GlobalRecycler;
import common.domain.RpcRequest;
import common.domain.RpcResponse;
import config.ReferenceConfig;
import lombok.extern.slf4j.Slf4j;
import protocol.api.InvokeParam;
import protocol.api.Invoker;
import protocol.api.support.AbstractInvoker;
import protocol.api.support.RpcInvokeParam;
import proxy.api.RpcProxyFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: fnbory
 * @Date: 2019/10/2 16:05
 */
@Slf4j
public abstract  class AbstractProxyFactory implements RpcProxyFactory {

    protected Map<Class<?>, Object> cache = new ConcurrentHashMap<>();

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
                RpcResponse response= GlobalRecycler.reuse(RpcResponse.class);
                try {
                    Method method=proxy.getClass().getMethod(invokeParam.getMethodName(),invokeParam.getParameterTypes());
                    response.setRequestId(invokeParam.requestId());
                    response.setResult(method.invoke(proxy,invokeParam.getParameters()));
                } catch (Exception e) {
                    response.setCause(e);
                }
                return response;
            }
        };
    }

    @Override
    public <T> T createProxy(Invoker invoker) {
        if(cache.containsKey(invoker.interfaceClass())){
            return (T)cache.get(invoker.interfaceClass());
        }
        T t=doCreateProxy(invoker.interfaceClass(),invoker);
        cache.put(invoker.interfaceClass(),t);
        return t;
    }

    protected abstract  <T> T doCreateProxy(Class interfaceClass, Invoker invoker);

    public Object invokeProxyMethod(Invoker invoker,Method method,Object[] args){
        Class<?>[] parameterTypes=method.getParameterTypes();
        String[] paramTypes=new String[parameterTypes.length];
        for(int i=0;i<parameterTypes.length;i++){
            paramTypes[i]=parameterTypes[i].getName();
        }
        return invokeProxyMethod(invoker,method.getDeclaringClass().getName(),method.getName(),paramTypes,args);
    }

    private Object invokeProxyMethod(Invoker invoker, String interfaceName, String methodName, String[] parameterTypes, Object[] args) {
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        RpcRequest request = GlobalRecycler.reuse(RpcRequest.class);
        log.info("调用服务：{}#{},parameterTypes:{},args:{}", interfaceName, methodName,parameterTypes,args);
        request.setRequestId(UUID.randomUUID().toString());
        request.setInterfaceName(interfaceName);
        request.setMethodName(methodName);
        request.setParameterTypes(parameterTypes);
        request.setParameters(args);
        RpcInvokeParam rpcInvokeParam=RpcInvokeParam.builder()
                .rpcRequest(request)
                .referenceConfig(ReferenceConfig.getReferenceConfigByInterfaceName(interfaceName))
                .build();
        RpcResponse response=invoker.invoke(rpcInvokeParam);
        Object result=null;
        if(response!=null){
            result=response.getResult();
        }
        response.recycle();
        return result;
    }
}
