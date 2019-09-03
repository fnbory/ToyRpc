package Server.spring.balance;

import Server.Provider;

import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/8/19 18:38
 */
public class RandomAl implements  LoadBalance{

    /**
     * 随机选一个，如果选中的节点不存在，默认选第一个
     * @param providers
     * @return
     * @throws Exception
     */

    @Override
    public Provider getProvider(List<Provider> providers) throws Exception {
        check(providers);
        int size=providers.size();
        Integer location=size(0,size-1);
        Provider provider=providers.get(location);
        if(provider==null){
            provider=providers.get(0);
        }
        return provider;
    }
}
