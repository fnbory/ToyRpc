package Server.spring.filters;

import Server.spring.serialization.Request;
import Server.spring.serialization.Response;

/**
 * @Author: fnbory
 * @Date: 2019/8/20 18:06
 */
public class LogFilter implements  Filter {
    @Override
    public Object filter(Request request, Response response, FilterChain filterChain) {
        // log

        return filterChain.donext(request,response);
    }
}
