package transport.api;

import common.domain.RpcRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 19:04
 */
public interface Server {

    void run();

    void handleRPCRequest(RpcRequest rpcRequest, ChannelHandlerContext ctx);

    void close();
}
