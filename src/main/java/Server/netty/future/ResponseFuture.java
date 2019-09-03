package Server.netty.future;

import Server.spring.serialization.Response;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author: fnbory
 * @Date: 2019/8/23 12:41
 */
@Setter
@Getter
public  class ResponseFuture {

    private final String requestId;

    private CountDownLatch countDownLatch=new CountDownLatch(1);

    private final Channel processChannel;

    private final long beginTimestamp=System.currentTimeMillis();

    private volatile boolean sendRequestOk=true;

    private volatile Throwable cause;

    private Response response;

    private CompletableFuture<Object> future;

    private long timeout;

    private Boolean async;

    public ResponseFuture(String requestId,long timeout,Channel channel,CompletableFuture future){
        this.requestId=requestId;
        this.timeout=timeout;
        this.processChannel=channel;
        this.future=future;
        async=future!=null;
    }

    public Response waitForResponse() throws InterruptedException{
        countDownLatch.await(timeout, TimeUnit.SECONDS);
        return response;
    }

    public void putResponse( Response response){
        this.response=response;
        countDownLatch.countDown();
    }
}

