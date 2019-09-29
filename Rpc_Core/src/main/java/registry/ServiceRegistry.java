package registry;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 19:42
 */
public interface ServiceRegistry {

    void init();

    void registry(String address,String interfaceName);
}
