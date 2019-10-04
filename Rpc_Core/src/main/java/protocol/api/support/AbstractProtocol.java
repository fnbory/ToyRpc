package protocol.api.support;

import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;
import config.GlobalConfig;
import config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import protocol.api.Protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 19:23
 */
@Slf4j
public abstract class AbstractProtocol implements Protocol {

    private GlobalConfig globalConfig;

    private Map<String, ServiceConfig> expoter=new HashMap<>();

    public void init(GlobalConfig globalConfig){
        this.globalConfig=globalConfig;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    protected void putExpoter(Class interfaceClass,ServiceConfig serviceConfig){
        expoter.put(interfaceClass.getName(),serviceConfig);
    }

    @Override
    public ServiceConfig referLocalService(String interfaceName) {
        if(!expoter.containsKey(interfaceName)){
            throw new Rpcexception(ERROR_ENUM.EXPOSED_SERVICE_NOT_FOUND,"未找到暴露的服务:{}", interfaceName);
        }
        return expoter.get(interfaceName);
    }

    @Override
    public void close() {

    }
}
