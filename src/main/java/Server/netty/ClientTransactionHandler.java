package Server.netty;

import Server.Provider;
import Server.ResultSet;
import Server.netty.future.ResponseFuture;
import Server.spring.serialization.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.CompleteFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: fnbory
 * @Date: 2019/9/7 19:53
 */

@Slf4j
@ChannelHandler.Sharable
public class ClientTransactionHandler extends SimpleChannelInboundHandler<Serializable> {


    private Provider provider;

    public ClientTransactionHandler(Provider provider){
        this.provider=provider;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String info=this.provider.buildInfo();
        if (log.isInfoEnabled()) {
            log.info("连接断开:{}", this.provider);
        }
        ChannelMap.remove(info);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Serializable serializable) throws Exception {
        if(serializable instanceof Response){
            Response response=(Response) serializable;
            ResponseFuture responseFuture= ResultSet.getResponseFuture(response.getRequestId());
            if(responseFuture==null){
                return;
            }
            if(response.getAsync()){
                CompletableFuture future=responseFuture.getFuture();
                if(future!=null){
                    if(response.getObject()!=null){
                        future.complete(response.getObject());
                    }
                    else{
                        assert  response.getException()!=null;
                        future.completeExceptionally(response.getException());
                    }
                }
                else{
                    responseFuture.putResponse(response);
                }
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx,evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Assert.notNull(this.provider,"服务提供者不能为空");
        String info = this.provider.buildInfo();
        if (log.isInfoEnabled()) {
            log.info("服务成功连接到:{}", this.provider);
        }
        ChannelMap.put(info,(SocketChannel)ctx.channel());
    }
}
