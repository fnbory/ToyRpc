package Server;

/**
 * @Author: fnbory
 * @Date: 2019/8/23 15:25
 */

import Server.netty.future.ResponseFuture;
import Server.spring.serialization.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 从zk找到provieder列表
 */
@Slf4j
public class ResultSet {
    public static ThreadFactory defaultThreadFactory(){
        return new MyThredFactory();
    }

    private static Map<String, Response> severMap=new ConcurrentHashMap<>(32);

    private static  ConcurrentHashMap<String, ResponseFuture> responseTable=new ConcurrentHashMap<>(256);

    public void put(String path,Response response){
        severMap.put(path,response);
    }

    public Response get(String path){
        return severMap.remove(path);
    }

    public static ResponseFuture getResponseFuture(String requestId){
        return responseTable.remove(requestId);
    }

    public static  ResponseFuture putResponseFuture(String requestId,ResponseFuture future){
        ResponseFuture put=responseTable.put(requestId,future);
        if(put!=null){
            log.warn("dubplicate requestId");
        }
        return future;
    }


    static {
        ScheduledExecutorService service= Executors.newScheduledThreadPool(1,new MyThredFactory
                ("scan-respTable"));
        service.scheduleAtFixedRate(()->
        {
            Iterator<Map.Entry<String,ResponseFuture>> iterator=responseTable.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,ResponseFuture> next=iterator.next();
                ResponseFuture future=next. getValue();
                long begintimeStamp=future.getBeginTimestamp();
                long timeout=future.getTimeout();
                if((begintimeStamp + timeout * 10 + 1000) <= System.currentTimeMillis()){
                    iterator.remove();
                }
            }
        },10,10, TimeUnit.SECONDS);
    }
}
