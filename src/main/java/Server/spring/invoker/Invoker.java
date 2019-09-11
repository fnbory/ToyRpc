package Server.spring.invoker;


import Server.spring.serialization.Request;
import lombok.Data;

/**
 * @Author: fnbory
 * @Date: 2019/8/20 17:10
 */
@Data
public  abstract class Invoker {

    protected Request request;

    public abstract Object invoke();

    public Invoker(Request request){
        this.request=request;
    }


}
