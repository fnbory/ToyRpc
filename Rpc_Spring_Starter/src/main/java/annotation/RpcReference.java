package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 17:35
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {

    boolean async() default false;
    boolean callback() default  false;
    boolean oneway() default  false;
    long timeout() default 3000;
    String callbackMethod() default "";
    int callbackParamIndex() default 1;
}
