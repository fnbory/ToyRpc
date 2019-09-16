package Server.netty;

import Server.ResultSet;
import Server.netty.request.ResponseTask;
import Server.spring.serialization.Request;
import Server.spring.serialization.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: fnbory
 * @Date: 2019/9/7 21:42
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerTransactionHandler extends SimpleChannelInboundHandler<Serializable> {

    private ExecutorService responseTask = Executors.newFixedThreadPool(10, ResultSet.defaultThreadFactory());

    private ApplicationContext applicationContext;

    public ServerTransactionHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("channel active :{}", ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (log.isWarnEnabled()) {
            log.warn("channel inactive :{}", ctx.channel());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent e=(IdleStateEvent) evt;
            log.warn("channel close [{}] [{}]", e.state(), e.isFirst());
            if(e.state()== IdleState.READER_IDLE){
                ctx.close();
            }
            else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.close();
            }
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }


    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Serializable serializable) throws Exception {
        if( serializable instanceof Request){
            Request request=(Request) serializable;
            if(request.getCode()==2){
                Response response = new Response();
                response.setRequestId(request.getRequestId());
                response.setAsync(Boolean.TRUE);
                response.setSuccess(Boolean.TRUE);
                response.setObject("PONG");
                channelHandlerContext.writeAndFlush(response);
            }
            else {
                responseTask.execute(new ResponseTask(request, channelHandlerContext, this.applicationContext));
            }
        }
    }
}
