package config;

import filter.Filter;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import protocol.api.Invoker;

import java.util.List;
import java.util.Map;
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



    public static <T> ReferenceConfig createReferenceConfig(String interfaceName,Class<T> interfaceClass){
        if(cache.containsKey(interfaceName)){
            if(cache.get(interfaceName))
        }
    }


    private boolean isDiff(boolean isAsync,boolean isCallback,boolean isOneway){

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
}
