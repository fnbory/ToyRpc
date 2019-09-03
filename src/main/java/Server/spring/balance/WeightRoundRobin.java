package Server.spring.balance;

import Server.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: fnbory
 * @Date: 2019/8/19 22:30
 */
public class WeightRoundRobin implements  LoadBalance{
    private  int cursize=0;

    private Lock lock=new ReentrantLock();

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
        Provider provider=null;
        try {
            lock.tryLock(100, TimeUnit.SECONDS);
            if(cursize>=providerList.size()){
                cursize=0;
            }
            provider=providerList.get(cursize);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cursize++;
            lock.unlock();
        }
        if(provider==null){
            provider=providers.get(0);
        }
        return provider;
    }
}
