package Server.spring.Annotation;

import java.lang.annotation.*;

/**
 * @Author: fnbory
 * @Date: 2019/8/17 23:05
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Service {

    Class<?> interfaceClass() default void.class;

    String interfaceName() default  "";

    String version() default "";

    int weight() default 0;

    String registry();

    String serialization() default "jdk";
}
