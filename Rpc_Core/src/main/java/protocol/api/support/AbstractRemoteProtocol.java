package protocol.api.support;

import transport.api.Server;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 18:46
 */
public abstract class AbstractRemoteProtocol extends AbstractProtocol {

    private Server server;

    protected void openServer() {
        if (server == null) {
            server = doOpenServer();
        }
    }

    protected  abstract Server doOpenServer();


}
