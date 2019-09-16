package Server.netty.request;

import Server.spring.filters.Filter;
import Server.spring.filters.FilterChain;
import Server.spring.filters.FilterManager;
import Server.spring.invoker.ServerInvoker;
import Server.spring.serialization.Request;
import Server.spring.serialization.Response;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * @Author: fnbory
 * @Date: 2019/9/9 21:28
 */
public class ResponseTask implements  Runnable {

    private Request request;

    private ChannelHandlerContext channelHandlerContext;

    private ApplicationContext applicationContext;


    public ResponseTask(Request request, ChannelHandlerContext channelHandlerContext, ApplicationContext applicationContext) {
        this.request = Objects.requireNonNull(request, "request不能为空");
        this.channelHandlerContext = Objects.requireNonNull(channelHandlerContext);
        this.applicationContext = Objects.requireNonNull(applicationContext);
    }

    @Override
    public void run() {
        Assert.notNull(request,"request不能为空");
        List<Filter> filters= FilterManager.instance.getProviderFilter();
        FilterChain chain=new FilterChain(filters,new ServerInvoker(this.request,this.applicationContext));
        Response response=new Response();
        response.setRequestId(this.request.getRequestId());
        response.setSuccess(true);
        Object object=chain.donext(request,response);
        response.setObject(object);
        response.setAsync(this.request.getAsync());
        if(object instanceof  Throwable){
            response.setObject(null);
            response.setException((Throwable) object);
        }
        channelHandlerContext.writeAndFlush(response);
    }
}
