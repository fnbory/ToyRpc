package Server.netty;

import Server.ResultSet;
import Server.netty.future.ResponseFuture;
import Server.spring.serialization.Request;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: fnbory
 * @Date: 2019/8/22 11:15
 */
public class ChannelMap {

    private static ScheduledExecutorService HEART_SCHEDULE_SERVICE = Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("heart"));

    private static Map<String, SocketChannel> channelmap=new HashMap<>();

    public static void put(String info,SocketChannel channel){
        channelmap.put(info,channel);
    }
    public static SocketChannel get(String info){
        return channelmap.get(info);
    }

    public static void remove(String info) {
        channelmap.remove(info);
    }

    static {
        HEART_SCHEDULE_SERVICE.scheduleAtFixedRate(() -> channelmap.forEach((k, socketChannel) -> {
            final Request request = new Request();
            request.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            request.setCode(2);
            request.setAsync(true);
            ChannelFuture write = socketChannel.writeAndFlush(request);
            write.addListener(future -> {
                //log.info("send heart to {}", k);
                final CompletableFuture<Object> completableFuture = new CompletableFuture<>();
                ResultSet.putResponseFuture(request.getRequestId(), new ResponseFuture( request.getRequestId()
                        , 10,socketChannel, completableFuture));
                completableFuture.whenComplete((result, exception) -> log.info("receive result [{}] [{}]", result, socketChannel));
            });
        }), 10, 10, TimeUnit.SECONDS);
    }



}
