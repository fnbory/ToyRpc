package config;

import cluster.LoadBalancer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 节点设置包括负载均衡策略和容错机制
 * @Author: fnbory
 * @Date: 2019/9/29 20:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClusterConfig {
    private String loadBalancer;

    private LoadBalancer loadBalancerInstance;

    private String faulttolerance;

    private FaultToleranceHandler faultToleranceHandler;




}
