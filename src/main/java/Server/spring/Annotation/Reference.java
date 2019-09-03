package Server.spring.Annotation;

import java.lang.annotation.*;

/**
 * @Author: fnbory
 * @Date: 2019/8/17 22:37
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE,ElementType.FIELD,ElementType.METHOD})
@Documented
public @interface Reference {

    Class<?> interfaceClass() default void.class;

    String interfaceName() default  "";

    String version() default "";

    String registry() default  "";

    int timeout() default  10;
}
