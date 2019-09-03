package Server.spring.filters;

import Server.spring.invoker.Invoker;
import Server.spring.serialization.Request;
import Server.spring.serialization.Response;

import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/8/20 16:51
 */
public class FilterChain {

    private List<Filter> filters;

    private Integer index=-1;

    private Invoker invoker;

    public Object donext(Request request, Response response){
        if(index>=filters.size()-1){
            return invoker.invoke();
        }
        return filters.get(++index).filter(request,response,this);
    }

    public FilterChain(List<Filter> filters,Invoker invoker){
        this.filters=filters;
        this.invoker=invoker;
    }
}
