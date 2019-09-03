package Server.spring.invoker;

import Server.Provider;
import Server.ResultSet;
import Server.netty.ChannelMap;
import Server.netty.future.ResponseFuture;
import Server.netty.request.Client;
import Server.netty.request.NettyClient;
import Server.spring.serialization.Request;
import Server.spring.serialization.Response;
import Server.spring.serialization.SerializationFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import org.springframework.util.Assert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: fnbory
 * @Date: 2019/8/22 10:12
 */
public class ClientInvoker extends  Invoker{

    public ClientInvoker(Request request){
        super(request);
    }

    @Override
    public Object invoke() {
        Assert.notNull(request,"request 不能为空");

        String info=request.getProvider().buildInfo();
        AtomicReference<SocketChannel> channel=new AtomicReference<>(ChannelMap.get(info));
        Provider provider=request.getProvider();
        CountDownLatch countDownLatch=new CountDownLatch(1);
        if(channel.get()==null){
            Client client=new NettyClient(provider, SerializationFactory.resolve(request.getSerialization(),request.getServiceName()));
            ChannelFuture future=client.connect(provider.getHost(),provider.getPort());
            future.addListener(future1 -> {
               if(future1.isSuccess()){
                   channel.set(ChannelMap.get(info));
                   countDownLatch.countDown();
               }
            });
        }
        if(channel.get()==null){
            try {
                countDownLatch.await(1000, TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException("连接远程服务中断");
            }
            if(channel.get()==null){
                throw new RuntimeException("\"链接远程服务\" + provider.getHost() + \":\" + provider.getPort() + \"失败\"");
            }
        }
        write(channel);
        if(!request.getAsync()){
            try {
                ResponseFuture responseFuture= ResultSet.putResponseFuture(request.getRequestId(),new ResponseFuture(
                        request.getRequestId(),request.getTimeout(),channel.get(),null));
                Response response=responseFuture.waitForResponse();
                if(response==null){
                    throw  new RuntimeException("timeout exception:" + request.getTimeout());
                }
                if (response.getSuccess()) {
                    return response.getObject();
                }
                Throwable exception = response.getException();
                if (exception != null) {
                    throw new RuntimeException(exception);
                }
                throw new RuntimeException("unknown exception");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            CompletableFuture<Object> future=new CompletableFuture();
            ResultSet.putResponseFuture(request.getRequestId(),new ResponseFuture(
                    request.getRequestId(),request.getTimeout(),channel.get(),null));
            return future;
        }

    }

    public ChannelFuture write(AtomicReference<SocketChannel> channel){
        SocketChannel socketChannel=channel.get();
        return socketChannel.writeAndFlush(request);
    }


}
