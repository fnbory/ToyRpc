package Server.spring.invoker;

import Server.netty.cache.ServiceCache;
import Server.spring.serialization.Request;
import com.sun.xml.internal.ws.util.CompletedFuture;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: fnbory
 * @Date: 2019/8/20 19:09
 */
public class ServerInvoker extends  Invoker {

    private ApplicationContext applicationContext;

    private static AtomicInteger atomicInteger=new AtomicInteger(1);

    @Override
    public Object invoke() {
        Assert.notNull(request,"request不能为空");
        String serviceName=request.getServiceName();
        String methodName=request.getMethodName();
        Class<?>[] parameters=request.getParameters();
        Object[] args=request.getArgs();
        try{
            Object service= ServiceCache.getService(serviceName);
            if(service==null){
                Class<?> aClass=ClassUtils.forName(serviceName,ClassUtils.getDefaultClassLoader());
                service=applicationContext.getBean(aClass);
                ServiceCache.put(serviceName,service);
            }
            Method method=service.getClass().getDeclaredMethod(methodName,parameters);
            Object invoke=method.invoke(service,args);
            if(invoke instanceof CompletedFuture){
                CompletableFuture future= (CompletableFuture) invoke;
                Object o=future.get();
                System.out.println("次数"+atomicInteger.getAndIncrement());
                return o;
            }
            System.out.println("次数"+atomicInteger.getAndIncrement());
            return invoke;

        }
        catch (ClassNotFoundException| IllegalAccessException | NoSuchMethodException e){
            return e;
        }
        catch (InvocationTargetException ex){
            return ex;
        }
        catch (Exception exx){
            return  exx;
        }
    }

    public ServerInvoker(Request request, ApplicationContext applicationContext){
        super(request);
        this.applicationContext=applicationContext;
    }



}
