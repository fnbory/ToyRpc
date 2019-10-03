package transport.api.support;

import config.GlobalConfig;
import transport.api.Server;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 19:11
 */
public abstract class AbstractServer implements Server {

    private GlobalConfig globalConfig;

    public void init(GlobalConfig globalConfig){
        this.globalConfig=globalConfig;
        doinit();
    }

    protected GlobalConfig getGlobalConfig(){
        return globalConfig;
    }

    protected abstract void doinit();


}
