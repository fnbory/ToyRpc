package beanpostptocess;

import annotation.RpcService;
import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;
import config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 18:38
 */
@Slf4j
public class ProviderPostProcess  extends AbstractPostProcessor {


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
        if(interfaceClass==null){
            Class[] interfaces=beanClass.getInterfaces();
            if(interfaces.length>=1){
                interfaceClass=interfaces[0];
            }
            else{
                throw new Rpcexception(ERROR_ENUM.SERVICE_DID_NOT_IMPLEMENT_ANY_INTERFACE,"该服务 {} 未实现任何服务接口", beanClass);
            }
        }
        ServiceConfig serviceConfig=ServiceConfig.builder()
                        .interfaceClass((Class) interfaceClass)
                        .interfaceName(interfaceClass.getName())
                        .ref(bean).build();
        initconfig(serviceConfig);
        serviceConfig.export();
        log.info("暴露服务:{}", interfaceClass);
        return bean;
    }


}
