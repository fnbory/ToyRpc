package Server.netty.request;

import Server.netty.ServerTransactionHandler;
import Server.netty.serilize.MessageDecoder;
import Server.netty.serilize.MessageEncoder;
import Server.spring.serialization.Iserialization;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

/**
 * @Author: fnbory
 * @Date: 2019/9/7 21:26
 */
public class NettyServer implements Server {

    private NioEventLoopGroup boss;

    private NioEventLoopGroup work;

    @Getter
    @Setter
    private Iserialization iserialization;

    @Getter
    @Setter
    private ApplicationContext applicationContext;

    private ChannelFuture channelFuture;


    public NettyServer(Iserialization iserialization,ApplicationContext applicationContext){
        this.applicationContext=applicationContext;
        this.iserialization=iserialization;
    }
    @Override
    public void bind(Integer port) {
        boss=new NioEventLoopGroup();
        work=new NioEventLoopGroup();

        ServerBootstrap bootstrap =new ServerBootstrap();
        final ServerTransactionHandler serverTransactionHandler = new ServerTransactionHandler(this.applicationContext);
        try {
            bootstrap.group(boss,work).channel(NioServerSocketChannel.class).handler(new LoggingHandler())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MessageDecoder(iserialization))
                                    .addLast(new MessageEncoder(iserialization))
                                    .addLast(new IdleStateHandler(20,40,60))
                                    .addLast(serverTransactionHandler);
                        }
                    }).option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true);
            channelFuture= bootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unRegister() throws InterruptedException {

    }
}
