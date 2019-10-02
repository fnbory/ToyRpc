package protocol.api.support;

import config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import protocol.api.Protocol;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 19:23
 */
@Slf4j
public class AbstractProtocol implements Protocol {

    private GlobalConfig globalConfig;

    public void init(GlobalConfig globalConfig){
        this.globalConfig=globalConfig;
    }
}
