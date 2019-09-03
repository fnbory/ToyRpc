package Server.spring.proxy;

import Server.Provider;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Author: fnbory
 * @Date: 2019/8/19 22:52
 */
public class ProviderSet {
    private static Map<String, List<Provider>>  map=new HashMap<>(32);

    public static void put(String serviceName,List<Provider> providers){
        Objects.requireNonNull(serviceName);
        Objects.requireNonNull(providers);
        List<Provider> providerList=map.get(serviceName);
        if(providerList==null){
            providerList=new ArrayList<>();
            map.put(serviceName,providers);
        }
        else {
            providerList.clear();
        }
        providerList.addAll(providers);
    }

    public static  List<Provider> getAll(String serviceName){
        return map.get(serviceName)==null?new ArrayList<Provider>():map.get(serviceName);
    }

    public static void reset(String serviceName,List<Provider> providers){
        Assert.notNull(serviceName,"服务不能为空");
        map.remove(serviceName);
        if(CollectionUtils.isEmpty(providers)){
            //log.warn("all providers has been shutdowned"+serviceName);
            return;
        }
        put(serviceName,providers);
    }



}
