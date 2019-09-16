package RpcTest;

import Server.spring.Annotation.Consumer;
import Server.spring.filters.Filter;
import Server.spring.filters.FilterChain;
import Server.spring.serialization.Request;
import Server.spring.serialization.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: fnbory
 * @Date: 2019/9/15 11:35
 */
@Consumer
@Slf4j
public class CheckFilter implements Filter {
    @Override
    public Object filter(Request request, Response response, FilterChain filterChain) {
        log.info("消费者过滤测试{}"+request);
        return filterChain.donext(request,response);
    }
}
