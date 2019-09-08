package Server.spring.filters;

import Server.spring.serialization.Request;
import Server.spring.serialization.Response;
import org.springframework.lang.Nullable;

/**
 * @Author: fnbory
 * @Date: 2019/8/19 22:04
 */
public interface Filter {

    Object filter(Request request, @Nullable Response response, FilterChain filterChain);

}
