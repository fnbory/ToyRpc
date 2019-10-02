package registry;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 19:42
 */
public interface ServiceRegistry {

    public static  String DEFAULT_PORT="8000";

    void init();

    void registry(String address,String interfaceName);

    void close();
}
