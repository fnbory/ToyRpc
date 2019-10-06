package common.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import protocol.api.Invoker;

import java.util.concurrent.Future;

/**
 * @Author: fnbory
 * @Date: 2019/10/6 21:14
 */
@NoArgsConstructor
@Setter
@Getter
public class RPCThreadLocalContext {

    private static final ThreadLocal<RPCThreadLocalContext>  RPC_CONTEXT=new ThreadLocal(){
        @Override
        protected Object initialValue() {
            return new RPCThreadLocalContext();
        }
    };

    private Future future;

    private Invoker invoker;

    public static RPCThreadLocalContext getContext(){
        return RPC_CONTEXT.get();
    }
}
