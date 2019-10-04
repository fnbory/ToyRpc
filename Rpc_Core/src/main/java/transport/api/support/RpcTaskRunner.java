package transport.api.support;

import common.domain.Message;
import common.domain.RpcRequest;
import common.domain.RpcResponse;
import config.ServiceConfig;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import transport.api.converter.MessageConverter;

/**
 * @Author: fnbory
 * @Date: 2019/10/4 17:17
 */
@AllArgsConstructor
@Slf4j
public class RpcTaskRunner implements Runnable {

    private ChannelHandlerContext ctx;

    private RpcRequest rpcRequest;

    private ServiceConfig serviceConfig;

    private MessageConverter messageConverter;



    @Override
    public void run() {
        if(serviceConfig.isCallback()){
            try {
                handle(rpcRequest);
                //rpcRequest.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        RpcResponse rpcResponse=GlobalRecycler.reuse(RpcResponse.class);
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        try {
            Object result=handle(rpcRequest);
            rpcResponse.setResult(result);
        } catch (Throwable t) {
            rpcResponse.setCause(t);
        }
        log.info("已调用完毕服务，结果为: {}", rpcResponse);
        if(!serviceConfig.isCallbackInterface()){
            Object data=messageConverter.convert2Object(Message.buildResponse(rpcResponse));
            ctx.writeAndFlush(data);
        }
    }

    private Object handle(RpcRequest rpcRequest) {
        Object serviceBean=serviceConfig.getRef();
        Class serviceClass=serviceBean.getClass();
        String methodName=rpcRequest
    }
}
