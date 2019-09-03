package Server.netty.cache;

import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: fnbory
 * @Date: 2019/8/20 21:41
 */
public class ServiceCache {

    private static Map<String,Object>  map=new ConcurrentHashMap<>();

    public static void put(String key,Object value){
        Assert.notNull(key,"key 不能为空");
        Assert.notNull(value,"value 不能为空");
        if(map.get(key)==null){
            map.put(key,value);
        }
        else{
            throw  new RuntimeException("dublicate");
        }
    }

    public static  Object getService(String service){
        Assert.notNull(service,"service 不能为空");
        return map.get(service);
    }
}
