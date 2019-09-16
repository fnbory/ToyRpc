package RpcTest;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author: fnbory
 * @Date: 2019/9/14 0:09
 */
@Server.spring.Annotation.Provider
public class Provider {
    public static void main(String[] args) throws  InterruptedException {
        ClassPathXmlApplicationContext applicationContext=new ClassPathXmlApplicationContext("classpath*:abc.xml");
        Thread.sleep(10000000L);
    }
}
