package Invocation.api;

import common.domain.RpcRequest;
import common.domain.RpcResponse;
import common.exception.Rpcexception;
import protocol.api.InvokeParam;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @Author: fnbory
 * @Date: 2019/10/7 15:57
 */
public interface Invocation {

    RpcResponse invoke(InvokeParam invokeParam, Function<RpcRequest, Future<RpcResponse>> requestProcessor) throws Rpcexception;
}
