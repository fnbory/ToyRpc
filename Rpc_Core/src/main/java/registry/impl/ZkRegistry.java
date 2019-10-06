package registry.impl;

import common.Constant.CharSetConst;
import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;
import config.RegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import registry.AbstractServiceRegistry;
import registry.api.ServiceURL;
import registry.api.ServiceURLAddOrUpdateCallback;
import registry.api.ServiceURLRemovalCallback;
import registry.support.ZkSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 14:25
 */
@Slf4j
public class ZkRegistry extends AbstractServiceRegistry {

    private ZkSupport zkSupport;

    private static long TEN_SEC = 10000000000L;
    private static final String ZK_REGISTRY_PATH = "/toy";

    private  volatile  Thread discoveringThread;

    public ZkRegistry(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    @Override
    public void init() {
        zkSupport = new ZkSupport();
        zkSupport.connect(registryConfig.getAddress());
    }

    @Override
    public void discover(String interfaceName, ServiceURLRemovalCallback callback, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback) {
        log.info("discovering...");
        this.discoveringThread = Thread.currentThread();
        watchInterface(interfaceName,callback,serviceURLAddOrUpdateCallback);
        log.info("开始Park... ");
        LockSupport.parkNanos(this,TEN_SEC);
        log.info("Park结束");
    }

    private void watchInterface(String interfaceName, ServiceURLRemovalCallback callback, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback) {
        try {
            String path=generatePath(interfaceName);
            List<String> addresses=zkSupport.getChildren(path, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType()== Event.EventType.NodeChildrenChanged){
                        watchInterface(interfaceName,callback,serviceURLAddOrUpdateCallback);
                    }
                }
            });
            log.info("interfaceName:{} -> addresses:{}", interfaceName, addresses);
            List<ServiceURL> dataList=new ArrayList<>();
            for(String node:addresses){
                dataList.add(watchService(interfaceName,node,serviceURLAddOrUpdateCallback));
            }
            log.info("node data: {}", dataList);
            callback.removeNotExisted(dataList);
            LockSupport.unpark(discoveringThread);
        } catch (KeeperException | InterruptedException e) {
            throw new Rpcexception(ERROR_ENUM.REGISTRY_ERROR,"ZK故障", e);
        }
    }

    private ServiceURL watchService(String interfaceName, String address, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback) {
        String path=generatePath(interfaceName);
        try {
            byte[] bytes=zkSupport.getData(path+"/"+address,new Watcher(){
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType()== Event.EventType.NodeDataChanged){
                        watchService(interfaceName,address,serviceURLAddOrUpdateCallback);
                    }
                }
            });
            ServiceURL serviceURL=ServiceURL.parse(new String(bytes, CharSetConst.UTF_8));
            serviceURLAddOrUpdateCallback.addOrUpdate(serviceURL);
            return serviceURL;
        } catch (KeeperException | InterruptedException e) {
            throw new Rpcexception(ERROR_ENUM.REGISTRY_ERROR,"ZK故障", e);        }
    }

    @Override
    public void registry(String address, String interfaceName) {
        String path=generatePath(interfaceName);
        try {
            zkSupport.createPathIfAbsent(path, CreateMode.PERSISTENT);
        } catch (KeeperException |InterruptedException e) {
            //throw new RPCException(ErrorEnum.REGISTRY_ERROR,"ZK故障", e);
        }
        zkSupport.createNodeIfAbsent(address, path);
    }

    private String generatePath(String interfaceName) {
        return new StringBuilder(ZK_REGISTRY_PATH).append("/").append(interfaceName).toString();
    }

    @Override
    public void close() {
        zkSupport.close();
    }
}
