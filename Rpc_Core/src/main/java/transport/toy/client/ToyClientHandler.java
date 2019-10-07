package transport.toy.client;

import common.domain.Message;
import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import transport.api.Client;
import transport.toy.constant.ToyConstant;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: fnbory
 * @Date: 2019/10/7 17:13
 */
@Slf4j
public class ToyClientHandler extends SimpleChannelInboundHandler<Message> {

    private Client client;

    private AtomicInteger timeoutCount = new AtomicInteger(0);


    public ToyClientHandler(Client client){
        this.client=client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端与服务器{}通道已开启...", client.getServiceURL().getAddress());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (timeoutCount.getAndIncrement() >= ToyConstant.HEART_BEAT_TIME_OUT_MAX_TIME) {
                // 尝试重连,当前handler生命周期已经结束
                client.handleException(new Rpcexception(ERROR_ENUM.APP_CONFIG_FILE_ERROR.HEART_BEAT_TIME_OUT_EXCEED,"{} 超过心跳重试次数",ctx.channel()));
            } else {
                log.info("超过指定时间未发送数据，客户端主动发送心跳信息至{}", client.getServiceURL().getAddress());
                ctx.writeAndFlush(Message.PING_MSG);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("客户端捕获到异常");
        cause.printStackTrace();
        log.info("与服务器{} 的连接断开", client.getServiceURL().getAddress());
        client.handleException(cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        log.info("接收到服务器 {} 响应: {}", client.getServiceURL().getAddress(), message);
        timeoutCount.set(0);
        //服务器不会PING客户端
        if (message.getType() == Message.PONG) {
            log.info("收到服务器的PONG心跳响应");
        } else if (message.getType() == Message.RESPONSE) {
            client.handleRPCResponse(message.getResponse());
        } else if (message.getType() == Message.REQUEST) {
            client.handleCallbackRequest(message.getRpcRequest(), ctx);
        }
    }
}
