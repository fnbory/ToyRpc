package protocol.api.support;

import common.domain.RpcRequest;
import common.domain.RpcResponse;
import common.enumeration.ERROR_ENUM;
import common.enumeration.InvocationType;
import common.exception.Rpcexception;
import config.GlobalConfig;
import filter.Filter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import protocol.api.InvokeParam;
import protocol.api.Invoker;
import registry.api.ServiceURL;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 16:19
 */
@Setter
@Slf4j
public abstract class   AbstractInvoker<T> implements Invoker {

    private Class interfaceClass;

    private String interfaceName;

    @Getter
    private GlobalConfig globalConfig;


    @Override
    public Class interfaceClass() { return interfaceClass; }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public ServiceURL getServiceURL() { return ServiceURL.DEFAULT_SERVICE_URL; }

    @Override
    public boolean isAvailable() { return true; }

    public Invoker buildFilterChain(List<Filter> filters){
        return new InvokerDelegate(this){
            private ThreadLocal<AtomicInteger> filterIndex=new ThreadLocal(){
                @Override
                protected Object initialValue() {
                    return new AtomicInteger(0);
                }
            };

            @Override
            public RpcResponse invoke(InvokeParam invokeParam) throws Rpcexception {
                log.info("filterIndex:{}, invokeParam:{}", filterIndex.get().get(), invokeParam);
                final Invoker invokerWrappedFilters=this;
                if(filterIndex.get().get()<filters.size()){
                    return filters.get(filterIndex.get().getAndIncrement()).invoke(new AbstractInvoker() {
                        @Override
                        public Class interfaceClass() {
                            return getDelegate().interfaceClass();
                        }

                        @Override
                        public String getInterfaceName() {
                            return getDelegate().getInterfaceName();
                        }

                        @Override
                        public ServiceURL getServiceURL() {
                            return getDelegate().getServiceURL();
                        }

                        @Override
                        public RpcResponse invoke(InvokeParam invokeParam) {
                            return invokerWrappedFilters.invoke(invokeParam);
                        }
                    },invokeParam);
                }
                filterIndex.get().set(0);
                return getDelegate().invoke(invokeParam);
            }
        };
    }

    @Override
    public RpcResponse invoke(InvokeParam invokeParam) {
        Function<RpcRequest, Future<RpcResponse>> logic=getProcessor();
        if(logic==null){
            throw new Rpcexception(ERROR_ENUM.GET_PROCESSOR_MUST_BE_OVERRIDE_WHEN_INVOKE_DID_NOT_OVERRIDE, "没有重写AbstractInvoker#invoke方法的时候，必须重写getProcessor方法");
        }
        return InvocationType.get(invokeParam).invoke(invokeParam,logic);
    }

    protected Function<RpcRequest, Future<RpcResponse>> getProcessor() {
        return null;
    }
}
