package Server.spring.balance;

import Server.Provider;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/8/19 19:39
 */
public class WeightRandom implements  LoadBalance {

    @Override
    public Provider getProvider(List<Provider> providers) throws Exception {
        check(providers);
        List<Provider> providerList=new ArrayList<>();
        for(Provider provider:providers){
            Integer weight=provider.getWeight();
            for(int i=0;i<weight;i++){
                providerList.add(provider.clone());
            }
        }
        Integer size=size(0,providerList.size()-1);
        Provider provider=providerList.get(size);
        return provider==null?providers.get(0):provider;
    }
}
