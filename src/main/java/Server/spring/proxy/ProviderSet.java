package Server.spring.proxy;

import Server.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 提供者集合
 * @Author: fnbory
 * @Date: 2019/8/19 22:52
 */
@Slf4j
public class ProviderSet {
    private static Map<String, List<Provider>>  map=new HashMap<>(32);

    /**
     *
     * @param serviceName   服务名
     * @param providers     服务提供者集合
     */

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
        List<Provider> providers = map.get(serviceName);
        if (providers == null || providers.size() == 0) {
            return new ArrayList<>();
        }
        return providers;
    }

    public static void reset(String serviceName,List<Provider> providers){
        Assert.notNull(serviceName,"服务不能为空");
        map.remove(serviceName);
        if(CollectionUtils.isEmpty(providers)){
            log.warn("all providers has been shutdowned"+serviceName);
            return;
        }
        put(serviceName,providers);
    }



}
