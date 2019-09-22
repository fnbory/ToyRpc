package Server.netty.request;

import io.netty.channel.ChannelFuture;

/**
 * @Author: fnbory
 * @Date: 2019/8/22 11:33
 */
public interface Client {


    ChannelFuture connect(String host,Integer port);

    void disConnect() throws InterruptedException;
}
