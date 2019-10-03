package transport.toy.server;

import common.domain.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import transport.api.Server;

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
}
