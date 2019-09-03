package Server.spring.balance;

import Server.Provider;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: fnbory
 * @Date: 2019/8/19 18:42
 */
public class RoundRobin implements LoadBalance{

    private  Integer cursize=0;

    private ReentrantLock lock=new ReentrantLock();
    /**
     * 轮询法
     * @param providers
     * @return
     * @throws Exception
     */

    @Override
    public Provider getProvider(List<Provider> providers) throws Exception {
        check(providers);
        Provider provider=null;
        try {
            lock.tryLock(1, TimeUnit.SECONDS);
            if(cursize>=providers.size()){
                cursize=0;
            }
            provider=providers.get(cursize);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            cursize++;
            lock.unlock();
        }
        if (provider==null){
            provider=providers.get(0);
        }
        return provider;
    }
}
