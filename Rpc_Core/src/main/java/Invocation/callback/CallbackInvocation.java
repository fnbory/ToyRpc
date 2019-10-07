package Invocation.callback;

import Invocation.api.support.AbstractInvocation;
import common.domain.RpcRequest;
import common.domain.RpcResponse;
import config.ReferenceConfig;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @Author: fnbory
 * @Date: 2019/10/7 17:36
 */
public class CallbackInvocation extends AbstractInvocation {

    @Override
    protected RpcResponse doInvoke(RpcRequest rpcRequest, ReferenceConfig referenceConfig, Function<RpcRequest, Future<RpcResponse>> requestProcessor) throws Throwable {
        return null;
    }
}
