package Server.spring.bean;

import Server.Consumer;
import Server.Provider;
import Server.spring.balance.LoadBalance;
import Server.spring.filters.Filter;
import Server.spring.filters.FilterChain;
import Server.spring.filters.FilterManager;
import Server.spring.invoker.ClientInvoker;
import Server.spring.proxy.ProviderSet;
import Server.spring.serialization.Request;
import Server.zk.ZkRegister;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: fnbory
 * @Date: 2019/8/17 23:19
 */
@Getter
@Setter
public class ReferenceBean implements FactoryBean, InitializingBean, ApplicationContextAware, Serializable {

    private String id;

    private String interfaceName;

    private String version;

    private Integer timeout=10;

    private Object object;

    private String loadBalance;

    private AtomicBoolean register=new AtomicBoolean(false);

    private ApplicationContext applicationContext;

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return object.getClass();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.build();
    }

    public void build() throws  ClassNotFoundException{
        ZkRegister zkRegister=applicationContext.getBean(ZkRegister.class);
        buildConsumer(zkRegister);
        List<Provider> discovers=zkRegister.discover(this.interfaceName, this.version);
        if (discovers == null || discovers.size() == 0) {
            throw new RuntimeException("服务端列表[" + this.interfaceName + "--" + this.version + "]为空");
        }
        zkRegister.subscribe(this.interfaceName);
        ProviderSet.put(this.interfaceName,discovers);
        Object object= Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{Class.forName(this.interfaceName)},
                new RpcInvocationHandler(this));
        Assert.notNull(object,"代理引用不能为空");
        this.object= object;
    }

    private void buildConsumer(ZkRegister zkRegister){
        if(register.compareAndSet(false,true)){
            Consumer consumer=new Consumer();
            consumer.setHost("127.0.0.1");
            consumer.setTimeout(this.timeout);
            consumer.setVersion(this.version);
            consumer.setServiceName(this.interfaceName);
            zkRegister.registerConsumer(consumer);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    class RpcInvocationHandler implements InvocationHandler{

        private String className;

        private String version;

        private LoadBalance loadBalance;

        private Integer timeout;

        RpcInvocationHandler(ReferenceBean referenceBean){
            Assert.notNull(referenceBean,"referenceBean不能为空");
            this.className=referenceBean.getInterfaceName();
            this.timeout=referenceBean.getTimeout();
            this.version=referenceBean.getVersion();
            loadBalance=LoadBalanceFactory.resolve(referenceBean.getLoadBalance());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            List<Provider> all= ProviderSet.getAll(this.className);
            if(CollectionUtils.isEmpty(all)){
                throw new RuntimeException("服务端列表+["+this.className+this.version+"]");
            }
            Provider provider=loadBalance.getProvider(all);
            Request request=Request.buildRequest(provider,args,timeout,method);
            request.setAsync(method.getReturnType().isAssignableFrom(CompletableFuture.class));
            List<Filter> filters= FilterManager.instance.getConsumerFilter();
            FilterChain filterChain=new FilterChain(filters,new ClientInvoker(request));
            return filterChain.donext(request,null);
         }
    }


}
