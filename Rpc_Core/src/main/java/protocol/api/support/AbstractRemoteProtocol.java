package protocol.api.support;

import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;
import lombok.extern.slf4j.Slf4j;
import registry.api.ServiceURL;
import transport.api.Client;
import transport.api.Server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 18:46
 */
@Slf4j
public abstract class AbstractRemoteProtocol extends AbstractProtocol {

    private Server server;

    private Map<String, Client> clients = new ConcurrentHashMap<>();

    private Map<String, Object> locks = new ConcurrentHashMap<>();

    protected void openServer() {
        if (server == null) {
            server = doOpenServer();
        }
    }

    public void closeEndpoint(String address){
        Client client = clients.remove(address);
        if (client != null) {
            log.info("首次关闭客户端:{}", address);
            client.close();
        } else {
            log.info("重复关闭客户端:{}", address);
        }
    }

    public final Client initClient(ServiceURL serviceURL){
        String address=serviceURL.getAddress();
        locks.putIfAbsent(address,new Object());
        synchronized (locks.get(address)){
            if(clients.containsKey(address)){
                return clients.get(address);
            }
            Client client=doInitClient(serviceURL);
            clients.put(address,client);
            locks.remove(address);
            return client;
        }
    }

    protected  abstract Server doOpenServer();

    protected abstract Client doInitClient(ServiceURL serviceURL);

    public void updateEndpointConfig(ServiceURL serviceURL) {
        if(!clients.containsKey(serviceURL.getAddress())){
            throw new Rpcexception(ERROR_ENUM.PROTOCOL_CANNOT_FIND_THE_SERVER_ADDRESS, "无法找到该地址{}", serviceURL);
        }
        clients.get(serviceURL.getAddress()).updateServiceConfig(serviceURL);
    }
}
