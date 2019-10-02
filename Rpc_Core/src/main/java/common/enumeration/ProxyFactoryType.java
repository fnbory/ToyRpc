package common.enumeration;

import proxy.JdkRpcProxyFactory;
import proxy.api.RpcProxyFactory;


/**
 * @Author: fnbory
 * @Date: 2019/10/2 15:45
 */
public enum  ProxyFactoryType implements  ExtentionBaseType{

    JDK(new JdkRpcProxyFactory()),;

    private RpcProxyFactory rpcProxyFactory;

    ProxyFactoryType(RpcProxyFactory rpcProxyFactory){
        this.rpcProxyFactory=rpcProxyFactory;
    }

    @Override
    public RpcProxyFactory getInstance() {
        return rpcProxyFactory;
    }
}
