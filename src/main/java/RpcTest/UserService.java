package RpcTest;

import java.util.concurrent.CompletableFuture;

/**
 * @Author: fnbory
 * @Date: 2019/9/14 0:14
 */
public interface UserService {
    String name();
    void noArg();
    CompletableFuture<String> good(String name);
}
