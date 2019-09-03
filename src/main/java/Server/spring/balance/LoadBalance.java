package Server.spring.balance;

import Server.Provider;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: fnbory
 * @Date: 2019/8/19 18:17
 */
public interface LoadBalance{

    Provider getProvider(List<Provider> providers) throws  Exception;

    default void check(List<Provider> providers){
        if(providers==null||providers.size()==0){
            throw new IllegalArgumentException("服务端列表不能为空");
        }
    }

    default  Integer size(Integer min,Integer max){
        return ThreadLocalRandom.current().nextInt(min,max);
    }
}
