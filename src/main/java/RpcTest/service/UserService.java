package RpcTest.service;

import RpcTest.domain.Page;
import RpcTest.domain.User;

import java.util.concurrent.CompletableFuture;

/**
 * @Author: fnbory
 * @Date: 2019/9/14 0:14
 */
public interface UserService {
    String name();
    void noArg();
    CompletableFuture<String> good(String name);

    boolean exitUser(String email);

    boolean createUser(User user);

    User getUser(long id);

    Page<User> listUser(int pageNo);
}
