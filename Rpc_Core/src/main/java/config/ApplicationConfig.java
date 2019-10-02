package config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import proxy.api.RpcProxyFactory;
import serialize.api.Serializer;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 16:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationConfig {
    // proxy名字（eg：JdkRpcProxy）
    private  String proxy;

    private String serialize;
    private Serializer serializerInstance;
    private RpcProxyFactory proxyFactoryInstance;
}
