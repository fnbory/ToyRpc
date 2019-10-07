package Invocation.Sync;

import Invocation.api.support.AbstractInvocation;
import common.domain.RpcRequest;
import common.domain.RpcResponse;
import config.ReferenceConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @Author: fnbory
 * @Date: 2019/10/7 16:07
 */
@Slf4j
public class SyncInvocation extends AbstractInvocation {

    @Override
    protected RpcResponse doInvoke(RpcRequest rpcRequest, ReferenceConfig referenceConfig, Function<RpcRequest, Future<RpcResponse>> requestProcessor) throws Throwable {
        Future<RpcResponse> future = requestProcessor.apply(rpcRequest);
        RpcResponse response = future.get(referenceConfig.getTimeout(), TimeUnit.MILLISECONDS);
        log.info("客户端读到响应:{}", response);
        return response;
    }
}
