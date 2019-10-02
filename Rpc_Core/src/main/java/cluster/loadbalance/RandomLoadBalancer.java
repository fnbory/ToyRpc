package cluster.loadbalance;


import cluster.support.AbstractLoadBalancer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public class RandomLoadBalancer extends AbstractLoadBalancer {


    @Override
    protected Invoker doSelect(List<Invoker> invokers, RPCRequest request) {
        if (invokers.size() == 0) {
            return null;
        }
        return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));

    }
}
