package config;

import protocol.api.Invoker;

import java.util.Map;

/**
 * @Author: fnbory
 * @Date: 2019/10/4 23:08
 */
public class ReferenceConfig<T> extends  AbstractConfig{

    private String interfaceName;

    private Class interfaceClass;

    private boolean isAsync;

    private boolean isOneWay;

    private boolean isCallback;

    private long timeout=3000;

    private volatile T ref;

    private boolean isGeneric;

    private volatile boolean initialized;

    private volatile Invoker invoker;

    private static Map<String,ReferenceConfig> cache=new



    public static <T> ReferenceConfig createReferenceConfig(String interfaceName,Class<T> interfaceClass){
        if(cache.containsKey(interfaceName)){
            if(cache.get(interfaceName))
        }
    }


    private boolean isDiff(boolean isAsync,boolean isCallback,boolean isOneway){

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
        invoker=getClusterConfig().getLoadBalancerInstance().referClustere(this);
        if(!isGeneric){
            ref=getClusterConfig()
        }
    }
}
