package annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 16:46
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component // ??
public @interface RpcService {
    Class<?> interfaceClass() default void.class;

}
