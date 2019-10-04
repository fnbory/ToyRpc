package transport.toy.server;

import common.domain.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import transport.api.Server;
import transport.toy.constant.ToyConstant;

import java.util.concurrent.atomic.AtomicInteger;

import static common.domain.Message.PING;
import static common.domain.Message.REQUEST;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 23:26
 */
@Slf4j
public class ToyServerHandler extends SimpleChannelInboundHandler<Message> {

    private Server server;
    private AtomicInteger timeoutCount = new AtomicInteger(0);

    public ToyServerHandler(Server server) {
        this.server = server;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        log.info("服务器已接收请求 {}，请求类型 {}", message, message.getType());
        timeoutCount.set(0);
        if (message.getType() == PING) {
            log.info("收到客户端PING心跳请求,对其响应PONG心跳");
            ctx.writeAndFlush(Message.PONG_MSG);
        } else if (message.getType() == REQUEST) {
            server.handleRPCRequest(message.getRpcRequest(), ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            cause.printStackTrace();
        } finally {
            ctx.close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            if(timeoutCount.getAndIncrement()>= ToyConstant.HEART_BEAT_TIME_OUT_MAX_TIME){
                ctx.close();
                log.info("超过丢失心跳的次数阈值，关闭连接");
            }
            else{
                log.info("超过规定时间服务器未收到客户端的心跳或正常信息");
            }
        }
        else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
