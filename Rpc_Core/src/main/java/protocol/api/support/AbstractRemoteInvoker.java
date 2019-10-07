package protocol.api.support;

import lombok.Getter;
import lombok.Setter;
import registry.api.ServiceURL;
import transport.api.Client;

/**
 * @Author: fnbory
 * @Date: 2019/10/6 17:28
 */

public abstract class AbstractRemoteInvoker extends AbstractInvoker {
    @Getter
    @Setter
    private Client client;

    @Override
    public ServiceURL getServiceURL() {
        return client.getServiceURL();
    }

    @Override
    public boolean isAvailable() {
        return client.isAvailable();
    }
}
