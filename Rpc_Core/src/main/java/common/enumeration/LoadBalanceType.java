package common.enumeration;

import cluster.loadbalance.*;
import cluster.support.AbstractLoadBalancer;

/**
 * @Author: fnbory
 * @Date: 2019/10/2 18:11
 */
public enum LoadBalanceType implements ExtentionBaseType {

    LEAST_ACTIVE(new LeastActiveLoadBalancer()),
    RANDOM(new RandomLoadBalancer()),
    CONSISTENT_HASH(new ConsistentHashLoadBalancer()),
    ROUND_ROBIN(new RoundRobinLoadBalancer()),
    WEIGHTED_RANDOM(new WeightedRandomLoadBalancer());

    LoadBalanceType(AbstractLoadBalancer loadBalancer){
        this.loadBalancer=loadBalancer;

    }

    private AbstractLoadBalancer loadBalancer;


    @Override
    public AbstractLoadBalancer getInstance() {
        return loadBalancer;
    }
}
