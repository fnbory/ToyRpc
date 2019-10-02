package cluster.loadbalance;


import cluster.support.AbstractLoadBalancer;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {
    private int index = 0;
    
    @Override
    protected Invoker doSelect(List<Invoker> invokers, RPCRequest request) {
         if(invokers.size() == 0) {
            return null;
        }
        Invoker result = invokers.get(index);
        index = (index + 1) % invokers.size();
        return result;
    }
}
