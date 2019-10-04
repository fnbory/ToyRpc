package transport.api.support.netty;

import common.domain.RpcRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import transport.api.converter.ServerMessageConverter;
import transport.api.support.AbstractServer;
import transport.api.support.RpcTaskRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 19:12
 */
@Slf4j
public abstract class AbstractNettyServer extends AbstractServer {

    private ChannelInitializer channelInitializer;

    private ServerMessageConverter serverMessageConverter;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private ChannelFuture channelFuture;
    

    @Override
    protected void doinit() {
        this.channelInitializer=initPipeline();
        // 消息转换器
        this.serverMessageConverter=initConverter();
    }

    protected abstract ServerMessageConverter initConverter();

    protected abstract ChannelInitializer initPipeline();

    @Override
    public void run() {
        log.info("支持EPOLL?:{}", Epoll.isAvailable());
        bossGroup=Epoll.isAvailable()?new EpollEventLoopGroup(1):new NioEventLoopGroup(1);
        workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        log.info("bossGroup:{},workerGroup:{}", bossGroup, workerGroup);
        try {
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(Epoll.isAvailable()? EpollServerSocketChannel.class: NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR,PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .option(ChannelOption.SO_SNDBUF,32*1024)
                    .option(ChannelOption.SO_RCVBUF,32*1024)
                    .option(ChannelOption.TCP_NODELAY,true);
            String host= InetAddress.getLocalHost().getHostAddress();
            this.channelFuture=bootstrap.bind(host,getGlobalConfig().getPort()).sync();
            log.info("服务器启动,当前服务器类型为:{}", this.getClass().getSimpleName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleRPCRequest(RpcRequest rpcRequest, ChannelHandlerContext ctx) {
        // 处理RpcRequest
        getGlobalConfig().getServerExecutor().submit(new RpcTaskRunner(ctx,rpcRequest,
                getGlobalConfig().getProtocol().referLocalService(rpcRequest.getInterfaceName()),serverMessageConverter));
    }

    @Override
    public void close() {
        getGlobalConfig().getRegistryConfig().close();
        if(workerGroup!=null){
            workerGroup.shutdownGracefully();
        }
        if(bossGroup!=null){
            bossGroup.shutdownGracefully();
        }
        if(channelFuture!=null){
            channelFuture.channel().close();
        }

    }
}
