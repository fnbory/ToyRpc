package Server.spring.bean;

import Server.spring.balance.*;
import org.springframework.util.StringUtils;

/**
 * @Author: fnbory
 * @Date: 2019/8/19 17:06
 */
public class LoadBalanceFactory {

    private LoadBalanceFactory(){}

    public static LoadBalance resolve(String loadBalance){
        if(StringUtils.isEmpty(loadBalance)){
            loadBalance="RoundRobin";
        }
        LoadBalance finalBalance;
        switch (loadBalance){
            case "RandomAl":
                finalBalance=new RandomAl();
                break;
            case "RoundRobin":
                finalBalance=new RoundRobin();
                break;
            case "SourceHash":
                finalBalance=new SourceHash();
                break;
            case  "WeightRandom":
                finalBalance=new WeightRandom();
                break;
            case "":
                finalBalance=new WeightRoundRobin();
                break;
            default:
                // log.warn("未知的负载均衡算法");
                finalBalance=new RoundRobin();
        }
        return finalBalance;
    }
}
