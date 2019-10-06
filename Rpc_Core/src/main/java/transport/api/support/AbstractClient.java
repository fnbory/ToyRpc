package transport.api.support;

import config.GlobalConfig;
import lombok.Getter;
import registry.api.ServiceURL;
import transport.api.Client;

/**
 * @Author: fnbory
 * @Date: 2019/10/6 20:43
 */
@Getter
public abstract class AbstractClient implements Client {

    private ServiceURL serviceURL;

    private GlobalConfig globalConfig;

    @Override
    public void updateServiceConfig(ServiceURL serviceURL) {
        this.serviceURL=serviceURL;
    }

    protected  abstract void connect();

    public void init(GlobalConfig globalConfig,ServiceURL serviceURL){
        this.globalConfig=globalConfig;
        this.serviceURL=serviceURL;
        connect();
    }


}
