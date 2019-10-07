package cluster;

import common.context.RPCThreadLocalContext;
import common.domain.RpcResponse;
import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;
import common.utils.InvokeParamUtil;
import config.GlobalConfig;
import config.ReferenceConfig;
import lombok.extern.slf4j.Slf4j;
import protocol.api.InvokeParam;
import protocol.api.Invoker;
import protocol.api.support.AbstractRemoteProtocol;
import protocol.injvm.InjvmProtocol;
import registry.api.ServiceURL;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: fnbory
 * @Date: 2019/10/5 14:45
 */
@Slf4j
public class ClusterInvoker<T> implements Invoker<T> {

    private Class<T> interfaceClass;

    private String interfaceName;

    private GlobalConfig globalConfig;

    private Map<String, Invoker> addressInvokers = new HashMap<>();

    public ClusterInvoker(Class<T> interfaceClass, String interfaceName, GlobalConfig globalConfig) {
        this.interfaceClass = interfaceClass;
        this.interfaceName = interfaceName;
        this.globalConfig = globalConfig;
        init();
    }

    private void init() {
        if (globalConfig.getProtocol() instanceof InjvmProtocol) {
            addOrUpdate(ServiceURL.DEFAULT_SERVICE_URL);
        } else {
            globalConfig.getServiceRegistry().discover(interfaceName, (newServiceURLs -> {
                removeNotExisted(newServiceURLs);
            }), (serviceURL -> {
                addOrUpdate(serviceURL);
            }));
        }
    }

    public void addOrUpdate(ServiceURL serviceURL) {

        if (addressInvokers.containsKey(serviceURL.getAddress())) {
            if (globalConfig.getProtocol() instanceof AbstractRemoteProtocol) {
                AbstractRemoteProtocol protocol = (AbstractRemoteProtocol) globalConfig.getProtocol();
                log.info("update config:{},当前interface为:{}", serviceURL, interfaceName);
                protocol.updateEndpointConfig(serviceURL);
            }
        } else {
            log.info("add invoker:{},serviceURL:{}", interfaceName, serviceURL);
            Invoker invoker = globalConfig.getProtocol().refer(ReferenceConfig.getReferenceConfigByInterfaceName(interfaceName), serviceURL);
            addressInvokers.put(serviceURL.getAddress(), invoker);
        }
    }

    public void removeNotExisted(List<ServiceURL> newServiceURLs) {
        // 最新的地址列表，当中没有的就是挂掉的
        Map<String, ServiceURL> newAddressesMap = newServiceURLs.stream().collect(Collectors.toMap(
                url -> url.getAddress(), url -> url
        ));
        for (Iterator<Map.Entry<String, Invoker>> iterator = addressInvokers.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Invoker> curr = iterator.next();
            if (!newAddressesMap.containsKey(curr.getKey())) {
                log.info("remove address:{},当前interface为:{}", curr.getKey(), interfaceName);
                if (globalConfig.getProtocol() instanceof AbstractRemoteProtocol) {
                    AbstractRemoteProtocol protocol = (AbstractRemoteProtocol) globalConfig.getProtocol();
                    protocol.closeEndpoint(curr.getKey());
                }
                iterator.remove();
            }
        }
    }

    public List<Invoker> getInvokers(){
        return new ArrayList<>(addressInvokers.values());
    }

    @Override
    public Class<T> interfaceClass() {
        return interfaceClass;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public RpcResponse invoke(InvokeParam invokeParam) {
        Invoker invoker=doSelect(getInvokers(),invokeParam);
        RPCThreadLocalContext.getContext().setInvoker(invoker);
        RpcResponse response=invoker.invoke(invokeParam);
        if(response==null){
            return null;
        }
        if(response.hasError()){
            Throwable cause=response.getCause();
            response.recycle();
            throw new Rpcexception(cause, ERROR_ENUM.SERVICE_INVOCATION_FAILURE, "invocation failed");
        }
        return response;
    }

    private Invoker doSelect(List<Invoker> availableInvokers, InvokeParam invokeParam) {
        if (availableInvokers.size() == 0) {
            log.error("未找到可用服务器");
            throw new Rpcexception(ERROR_ENUM.NO_SERVER_AVAILABLE, "未找到可用服务器");
        }
        Invoker invoker;
        if (availableInvokers.size() == 1) {
            invoker = availableInvokers.get(0);
            if (invoker.isAvailable()) {
                return invoker;
            } else {
                log.error("未找到可用服务器");
                throw new Rpcexception(ERROR_ENUM.NO_SERVER_AVAILABLE, "未找到可用服务器");
            }
        }
        invoker=globalConfig.getLoadBalancer().select(availableInvokers, InvokeParamUtil.extractRequestFromInvokeParam(invokeParam));
        if (invoker.isAvailable()) {
            return invoker;
        } else {
            availableInvokers.remove(invoker);
            return doSelect(availableInvokers, invokeParam);
        }

    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public ServiceURL getServiceURL() {
        throw new UnsupportedOperationException();
    }
}
