package common.domain;

import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;
import io.netty.util.Recycler;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: fnbory
 * @Date: 2019/10/5 12:52
 */
public class GlobalRecycler {

    private static Map<Class, Recycler> RECYCLER=new HashMap<>();

    static {
        RECYCLER.put(RpcRequest.class, new Recycler<RpcRequest>() {
            @Override
            protected RpcRequest newObject(Handle<RpcRequest> handle) {
                return new RpcRequest(handle);
            }
        });

        RECYCLER.put(RpcResponse.class, new Recycler() {
            @Override
            protected RpcResponse newObject(Handle handle) {
                return new RpcResponse(handle);
            }
        });
    }

    public static boolean isReusable(Class cls){
        return  RECYCLER.containsKey(cls);
    }

    public static <T> T reuse(Class<T> cls){
        if(isReusable(cls)){
            return (T)RECYCLER.get(cls).get();
        }
        throw new Rpcexception(ERROR_ENUM.RECYCLER_ERROR,"该类型对象不可复用:{}",cls);
    }
}
