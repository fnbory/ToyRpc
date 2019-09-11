package Server.spring.filters;

import Server.spring.serialization.Request;
import Server.spring.serialization.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: fnbory
 * @Date: 2019/8/20 18:06
 */
@Slf4j
public class LogFilter implements  Filter {
    @Override
    public Object filter(Request request, Response response, FilterChain filterChain) {
        if (log.isDebugEnabled()) {
            log.debug("rpc请求:{}", request);
        }

        return filterChain.donext(request,response);
    }
}
