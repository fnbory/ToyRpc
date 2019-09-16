package RpcTest;

import java.util.concurrent.CompletableFuture;

/**
 * @Author: fnbory
 * @Date: 2019/9/14 0:15
 */
public class UserServiceImpl implements UserService {
    @Override
    public String name() {
        return "1111111fan";
    }

    @Override
    public void noArg() {
        System.out.println("return void");

    }

    @Override
    public CompletableFuture<String> good(String name) {
        return CompletableFuture.supplyAsync(()->name);
    }
}
