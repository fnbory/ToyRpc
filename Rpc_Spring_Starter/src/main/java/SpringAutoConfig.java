import common.ExtentionLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 20:18
 */
@Configuration

public class SpringAutoConfig implements InitializingBean {

    @Autowired
    RpcProeprties proeprties;

  //  @Autowire  原则上不采用Spring来getBean，太重，使用者可能会导致版本冲突



    @Override
    public void afterPropertiesSet() throws Exception {
        ExtentionLoader extentionloader= ExtentionLoader.getInstance();
        extentionloader.loadResurce();
    }
}
