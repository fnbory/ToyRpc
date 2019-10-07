package transport.api.support.netty;

import common.context.RPCThreadSharedContext;
import common.domain.Message;
import common.domain.RpcRequest;
import common.domain.RpcResponse;
import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;
import config.ServiceConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import transport.api.converter.ClientMessageConverter;
import transport.api.converter.MessageConverter;
import transport.api.support.AbstractClient;
import transport.api.support.RpcTaskRunner;
import transport.toy.constant.ToyConstant;

import java.util.concurrent.*;

/**
 * @Author: fnbory
 * @Date: 2019/10/6 20:47
 */
@Slf4j
public abstract class AbstractNettyClient extends AbstractClient {

    private Bootstrap bootstrap;

    private EventLoopGroup group;

    private volatile Channel futureChannel;

    private volatile  boolean initialized=false;

    private volatile boolean destroyed = false;

    private volatile boolean closedFromOuter = false;

    private MessageConverter messageConverter;

    private static ScheduledExecutorService retryExecutor= Executors.newSingleThreadScheduledExecutor();

    private ConnectRetryer connectRetryer = new ConnectRetryer();

    /**
     * 与Handler相关
     *
     * @return
     */
    protected abstract ChannelInitializer initPipeline();

    /**
     * 与将Message转为Object类型的data相关
     *
     * @return
     */
    protected abstract ClientMessageConverter initConverter();

    @Override
    protected void connect() {
        if(initialized) return;
        this.messageConverter=initConverter();
        this.group= Epoll.isAvailable()?new EpollEventLoopGroup():new NioEventLoopGroup();
        this.bootstrap=new Bootstrap();
        this.bootstrap.group(group).channel(Epoll.isAvailable()? EpollSocketChannel.class: NioSocketChannel.class)
                .handler(initPipeline())
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true);
        try {
            doConnect();
        } catch (InterruptedException e) {
            log.error("与服务器的连接出现故障");
            e.printStackTrace();
            handleException(e);
        }

    }

    public synchronized void doConnect() throws InterruptedException{
        ChannelFuture future;
        String address = getServiceURL().getAddress();
        String host = address.split(":")[0];
        Integer port = Integer.parseInt(address.split(":")[1]);
        future = bootstrap.connect(host, port).sync();
        this.futureChannel = future.channel();
        log.info("客户端已连接至 {}", address);
        log.info("客户端初始化完毕");
        initialized = true;
        destroyed = false;
    }

    private void reconnect() throws Exception {
        // 避免多次异常抛出，导致多次reconnect
        if (destroyed) {
            connectRetryer.run();
        }
    }

    private class ConnectRetryer implements Runnable {

        @Override
        public void run() {
            if (!closedFromOuter) {
                log.info("重新连接中...");
                try {
                    // 先把原来的连接关掉
                    if (futureChannel != null && futureChannel.isOpen()) {
                        futureChannel.close().sync();
                    }
                    doConnect();
                } catch (Exception e) {
                    log.info("重新连接失败，{} 秒后重试", ToyConstant.HEART_BEAT_TIME_OUT);
                    retryExecutor.schedule(connectRetryer, ToyConstant.HEART_BEAT_TIME_OUT, TimeUnit.SECONDS);
                }
            } else {
                log.info("ZK无法检测到该服务器，不再重试");
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return initialized&&!destroyed;
    }

    @Override
    public void handleCallbackRequest(RpcRequest request, ChannelHandlerContext ctx) {
        ServiceConfig serviceConfig=RPCThreadSharedContext.getAndRemoveHandler(
                CallbackInvocation.generateCallbackHandlerKey(request));
        getGlobalConfig().getClientExecutor()
                .submit(new RpcTaskRunner(ctx, request, serviceConfig, messageConverter));
    }

    @Override
    public void handleRPCResponse(RpcResponse response) {
        CompletableFuture<RpcResponse> future = RPCThreadSharedContext.getAndRemoveResponseFuture(response.getRequestId());
        future.complete(response);
    }

    @Override
    public void handleException(Throwable throwable) {
        // destroy设置为true，客户端提交请求后会立即被拒绝
        destroyed = true;
        log.error("", throwable);
        log.info("开始尝试重新连接...");
        try {
            reconnect();
        } catch (Exception e) {
            close();
            log.error("", e);
            throw new Rpcexception(ERROR_ENUM.CONNECT_TO_SERVER_FAILURE, "重试多次后仍然连接失败,关闭客户端,放弃重试");
        }
    }

    @Override
    public Future<RpcResponse> submit(RpcRequest request) {
        if (!initialized) {
            connect();
        }
        if (destroyed || closedFromOuter) {
            throw new Rpcexception(ERROR_ENUM.SUBMIT_AFTER_ENDPOINT_CLOSED, "当前Endpoint: {} 关闭后仍在提交任务", getServiceURL().getAddress());
        }
        log.info("客户端发起请求: {},请求的服务器为: {}", request, getServiceURL().getAddress());
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        RPCThreadSharedContext.registerResponseFuture(request.getRequestId(), responseFuture);
        Object data = messageConverter.convert2Object(Message.buildRequest(request));
        this.futureChannel.writeAndFlush(data);
        log.info("请求已发送至{}", getServiceURL().getAddress());
        return responseFuture;
    }

    @Override
    public void close() {
        try {
            if (this.futureChannel != null && futureChannel.isOpen()) {
                this.futureChannel.close().sync();
            }
            destroyed = true;
            closedFromOuter = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (group != null && !group.isShuttingDown() && !group.isShutdown() && !group.isTerminated()) {
                group.shutdownGracefully();
            }
        }
    }
}
