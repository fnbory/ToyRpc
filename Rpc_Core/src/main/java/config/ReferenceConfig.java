package config;

import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;
import filter.Filter;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import protocol.api.Invoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: fnbory
 * @Date: 2019/10/4 23:08
 */
@Data
@Builder
@Slf4j
public class ReferenceConfig<T> extends  AbstractConfig{

    private String interfaceName;

    private Class interfaceClass;

    private boolean isAsync;

    private boolean isOneWay;

    private boolean isCallback;

    private long timeout=3000;

    private volatile T ref;

    private String callbackMethod;

    private int callbackParamIndex = 1;

    private boolean isGeneric;

    private volatile boolean initialized;

    private volatile Invoker invoker;

    private static Map<String,ReferenceConfig> cache=new ConcurrentHashMap<>();

    private List<Filter>filters;



    public static <T> ReferenceConfig createReferenceConfig(String interfaceName,Class<T> interfaceClass,
                                                            boolean isAsync,boolean isCallback,boolean isOneWay,
                                                            long timeout,String callbackMethod,int callbackParamIndex,
                                                            boolean isGeneric,List<Filter> filters){
        if(cache.containsKey(interfaceName)){
            if(cache.get(interfaceName).isDiff(isAsync,isCallback,isOneWay,timeout,callbackMethod,callbackParamIndex,
                    isGeneric)){
                throw new Rpcexception(ERROR_ENUM.SAME_INTERFACE_ONLY_CAN_BE_REFERRED_IN_THE_SAME_WAY, "同一个接口只能以相同的配置引用:{}", interfaceName);
            }
            return cache.get(interfaceName);
        }

        ReferenceConfig config=ReferenceConfig.builder()
                .interfaceName(interfaceName)
                .interfaceClass(interfaceClass)
                .isAsync(isAsync)
                .isCallback(isCallback)
                .isOneWay(isOneWay)
                .timeout(timeout)
                .callbackMethod(callbackMethod)
                .callbackParamIndex(callbackParamIndex)
                .isGeneric(isGeneric)
                .filters(filters!=null?filters:new ArrayList<>())
                .build();
        cache.put(interfaceName,config);
        return config;
    }


    private boolean isDiff(boolean isAsync,boolean isCallback,boolean isOneway,long timeout,String callbackMethod,
                           int callbackParamIndex,boolean isGeneric){
        if (this.isAsync != isAsync) {
            return true;
        }
        if (this.isCallback != isCallback) {
            return false;
        }
        if (this.isOneWay != isOneWay) {
            return true;
        }
        if (this.timeout != timeout) {
            return true;
        }
        if (!this.callbackMethod.equals(callbackMethod)) {
            return true;
        }
        if (this.callbackParamIndex != callbackParamIndex) {
            return true;
        }
        if (this.isGeneric != isGeneric) {
            return true;
        }
        return false;
    }

    public static ReferenceConfig getReferenceConfigByInterfaceName(String interfaceName){
        return cache.get(interfaceName);
    }

    public T get() {
        if(!initialized){
            init();
        }
        return ref;
    }

    private void init() {
        if(initialized){
            return;
        }
        initialized=true;
        invoker=getClusterConfig().getLoadBalancerInstance().referCluster(this);
        if(!isGeneric){
            ref=getApplicationConfig().getProxyFactoryInstance().createProxy(invoker);
        }
    }

    @Override
    public String toString() {
        return "ReferenceConfig{" +
                "interfaceName='" + interfaceName + '\'' +
                ", interfaceClass=" + interfaceClass +
                ", isAsync=" + isAsync +
                ", isOneWay=" + isOneWay +
                ", isCallback=" + isCallback +
                ", timeout=" + timeout +
                ", callbackMethod='" + callbackMethod + '\'' +
                ", callbackParamIndex=" + callbackParamIndex +
                ", ref=" + ref +
                ", initialized=" + initialized +
                ", filters=" + filters +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferenceConfig<?> that = (ReferenceConfig<?>) o;
        return isAsync == that.isAsync &&
                isOneWay == that.isOneWay &&
                isCallback == that.isCallback &&
                timeout == that.timeout &&
                callbackParamIndex == that.callbackParamIndex &&
                initialized == that.initialized &&
                Objects.equals(interfaceName, that.interfaceName) &&
                Objects.equals(interfaceClass, that.interfaceClass) &&
                Objects.equals(callbackMethod, that.callbackMethod) &&
                Objects.equals(ref, that.ref) &&
                Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {

        return Objects.hash(interfaceName, interfaceClass, isAsync, isOneWay, isCallback, timeout, callbackMethod, callbackParamIndex, ref, initialized, filters);
    }
}
