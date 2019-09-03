package Server.netty.request;

import Server.Provider;
import Server.netty.serilize.MessageDecoder;
import Server.netty.serilize.MessageEncoder;
import Server.spring.serialization.Iserialization;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: fnbory
 * @Date: 2019/8/22 11:32
 */
public class NettyClient implements Client{

    private NioEventLoopGroup work;

    private ChannelFuture channelFuture;

    @Getter
    @Setter
    private Provider provider;

    @Getter
    @Setter
    private Iserialization iSerialization;

    public NettyClient(Provider provider,Iserialization iserialization){
        this.provider=provider;
        this.iSerialization=iserialization;
    }

    @Override
    public ChannelFuture connect(String host, Integer port) {
        Bootstrap bootstrap=new Bootstrap();
        work=new NioEventLoopGroup(1,);
        bootstrap.group(work).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LoggingHandler())
                                .addLast(new MessageDecoder())
                                .addLast(new MessageEncoder())
                                .addLast(new nettyc())
                                .addLast();

                    }
                }).option(ChannelOption.SO_KEEPALIVE,true);
    }

    @Override
    public void disConnect() throws InterruptedException {

    }
}
