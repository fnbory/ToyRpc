package Server.netty.request;

import Server.Provider;
import Server.ResultSet;
import Server.netty.ClientTransactionHandler;
import Server.netty.serilize.MessageDecoder;
import Server.netty.serilize.MessageEncoder;
import Server.spring.serialization.Iserialization;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * @Author: fnbory
 * @Date: 2019/8/22 11:32
 */
@Slf4j
public class NettyClient implements Client{

    private NioEventLoopGroup work;

    private ChannelFuture channelFuture;

    @Getter
    @Setter
    private Provider provider;

    @Getter
    @Setter
    private Iserialization iserialization;

    public NettyClient(Provider provider,Iserialization iserialization){
        this.provider=provider;
        this.iserialization=iserialization;
    }

    @Override
    public ChannelFuture connect(String host, Integer port) {
        Bootstrap bootstrap=new Bootstrap();
        try {
            work=new NioEventLoopGroup(1, ResultSet.defaultThreadFactory());
            bootstrap.group(work).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LoggingHandler())
                                    .addLast(new MessageDecoder(iserialization))
                                    .addLast(new MessageEncoder(iserialization))
                                    .addLast(new NettyConnectManageHandler())
                                    .addLast(new ClientTransactionHandler(provider));

                        }
                    }).option(ChannelOption.SO_KEEPALIVE,true);
            channelFuture=bootstrap.connect(host,port).sync();
            return channelFuture;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void disConnect() throws InterruptedException {
        channelFuture.channel().closeFuture().sync();
        if(work!=null){
            work.shutdownGracefully();
        }
    }

    class NettyConnectManageHandler extends ChannelDuplexHandler {
        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            super.connect(ctx, remoteAddress, localAddress, promise);
            if (log.isDebugEnabled()) {
                log.debug("CONNECT SERVER [{}]", remoteAddress.toString());
            }
        }
    }
}
