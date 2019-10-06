package transport.api;

import common.domain.RpcRequest;
import common.domain.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import registry.api.ServiceURL;

import java.util.concurrent.Future;

/**
 * @Author: fnbory
 * @Date: 2019/10/6 17:14
 */
public interface Client {
    Future<RpcResponse> submit(RpcRequest request);

    void close();

    ServiceURL getServiceURL();

    void handleException(Throwable throwable);

    void handleCallbackRequest(RpcRequest request, ChannelHandlerContext ctx);

    void handleRPCResponse(RpcResponse response);

    boolean isAvailable();

    void updateServiceConfig(ServiceURL serviceURL);
}
