package Server.netty.request;

/**
 * @Author: fnbory
 * @Date: 2019/9/7 21:24
 */
public interface Server {

    void bind(Integer post);
    void unRegister() throws InterruptedException;
}
