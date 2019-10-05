package beanpostptocess;

import annotation.RpcReference;
import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;
import config.ReferenceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;

import java.lang.reflect.Field;

/**
 * @Author: fnbory
 * @Date: 2019/10/4 22:53
 */
@Slf4j
public class ConsumerPostProcessor extends AbstractPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass=bean.getClass();
        Field[] fields=beanClass.getDeclaredFields();
        for(Field field:fields){
            if(!field.isAccessible()){
                field.setAccessible(true);
            }
            Class interfaceClass=field.getType();
            RpcReference reference=field.getAnnotation(RpcReference.class);
            if(reference!=null){
                // 注入配置
                ReferenceConfig referenceConfig=ReferenceConfig.createReferenceConfig();
                initconfig(referenceConfig);
                try {
                    field.set(bean,referenceConfig.get());
                } catch (IllegalAccessException e) {
                    throw new Rpcexception(e, ERROR_ENUM.AUTOWIRE_REFERENCE_PROXY_ERROR,"set proxy failed");
                }
                log.info("注入依赖:{}",interfaceClass);
            }
        }
        return bean;
    }
}
