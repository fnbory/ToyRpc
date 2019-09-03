package Server;


import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Proxy;

/**
 * @Author: fnbory
 * @Date: 2019/8/17 19:18
 */
public class ProxyBean implements FactoryBean, InitializingBean {

    private Object object;

    private Class name;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.build();
    }

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return name;
    }

    public void build(){
        this.object= Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{this.name},(proxy,method,args)->{
            if(method.getName().equalsIgnoreCase("name")){
                return "调用name方法";
            }
            if(method.getName().equalsIgnoreCase("toString")){
                return "调用toString方法";
            }
            return "111";
        });
    }
}
