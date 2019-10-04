package registry.impl;

import config.RegistryConfig;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import registry.AbstractServiceRegistry;
import registry.support.ZkSupport;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 14:25
 */
public class ZkRegistry extends AbstractServiceRegistry {

    private ZkSupport zkSupport;

    private static long TEN_SEC = 10000000000L;
    private static final String ZK_REGISTRY_PATH = "/toy";

    public ZkRegistry(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    @Override
    public void init() {
        zkSupport = new ZkSupport();
        zkSupport.connect(registryConfig.getAddress());
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
