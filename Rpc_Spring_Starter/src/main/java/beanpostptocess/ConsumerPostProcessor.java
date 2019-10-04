package beanpostptocess;

import annotation.RpcReference;
import config.ReferenceConfig;
import org.springframework.beans.BeansException;

import java.lang.reflect.Field;

/**
 * @Author: fnbory
 * @Date: 2019/10/4 22:53
 */
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
                ReferenceConfig referenceConfig=ReferenceConfig.createReferenceConfig();
                initconfig(referenceConfig);
                field.set(bean,referenceConfig.get());
            }

        }
    }
}
