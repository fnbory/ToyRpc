package RpcTest;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: fnbory
 * @Date: 2019/9/14 0:11
 */
public class Consumer {

    static AtomicInteger atomicInteger=new AtomicInteger();
    public static void main(String[] args) throws  InterruptedException{
        ClassPathXmlApplicationContext applicationContext=new ClassPathXmlApplicationContext("classpath*:test.xml");
        UserService bean=applicationContext.getBean(UserService.class);
       for(int i=0;i<10;i++){
           System.out.println("********************************");
            System.out.println(bean.name()+"******************************************8");
       }
//            atomicInteger.getAndIncrement();
//        }
//        TimeUnit.SECONDS.sleep(10000000);
    }
}
