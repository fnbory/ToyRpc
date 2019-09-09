package Server.spring.filters;

import Server.spring.Annotation.Consumer;
import Server.spring.Annotation.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @Author: fnbory
 * @Date: 2019/8/20 17:23
 */
public class FilterManager {

    private static List<Filter> providerFilters=new ArrayList<>();
    private static List<Filter> consumerFilters=new ArrayList<>();

    public final static FilterManager instance=new FilterManager();

    public List<Filter> getProviderFilter(){
        return providerFilters;
    }

    public List<Filter> getConsumerFilter(){
        return consumerFilters;
    }

    private FilterManager(){}

    static {
        ServiceLoader<Filter> operations=ServiceLoader.load(Filter.class);
        for(Filter operation:operations){
            boolean provider=false;
            boolean consumer=false;
            if(operation.getClass().isAnnotationPresent(Provider.class)){
                provider=true;
                providerFilters.add(operation);
            }
            if(operation.getClass().isAnnotationPresent(Consumer.class)){
                consumer=true;
                consumerFilters.add(operation);
            }
            if(!provider&&!consumer){
                providerFilters.add(operation);
                consumerFilters.add(operation);
            }
        }

    }
}
