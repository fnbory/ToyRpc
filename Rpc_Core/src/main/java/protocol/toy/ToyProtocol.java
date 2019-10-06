package protocol.toy;

import config.ReferenceConfig;
import config.ServiceConfig;
import protocol.api.Invoker;
import protocol.api.support.AbstractRemoteProtocol;
import registry.api.ServiceURL;
import transport.api.Server;
import transport.toy.server.ToyServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 19:25
 */
public class ToyProtocol extends AbstractRemoteProtocol {

    @Override
    public <T> void export(Invoker invoker, ServiceConfig<T> serviceConfig) {
        openServer();
        try {
            serviceConfig.getRegistryConfig().getServiceRegistry().registry(InetAddress.getLocalHost().getHostAddress()
            +":"+getGlobalConfig().getPort(),serviceConfig.getInterfaceName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Server doOpenServer() {
        ToyServer toyServer=new ToyServer();
        toyServer.init(getGlobalConfig());
        toyServer.run();
        return toyServer;
    }

    @Override
    public Invoker refer(ReferenceConfig referenceConfig, ServiceURL serviceURL) {
        ToyInvoker toyInvoker=new ToyInvoker();
    }
}
