package beanpostptocess;

import annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import registry.ServiceRegistry;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 18:38
 */

public class ProviderPostProcess  implements BeanPostProcessor {

    // 后置处理该bean

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass=bean.getClass();
        if(!beanClass.isAnnotationPresent(RpcService.class)){
            return bean;
        }
        /**
         * 注册服务，暴露服务
         */
        // 获取注解中的内容
        RpcService rpcService=beanClass.getAnnotation(RpcService.class);
        Class<?> interfaceClass=rpcService.interfaceClass();
        ServiceRegistry.
        return bean;
    }


}
