package filter;

import common.domain.RpcResponse;
import protocol.api.InvokeParam;
import protocol.api.Invoker;

/**
 * @Author: fnbory
 * @Date: 2019/10/5 23:44
 */
public interface Filter {
    RpcResponse invoke(Invoker invoker, InvokeParam invokeParam);
}
