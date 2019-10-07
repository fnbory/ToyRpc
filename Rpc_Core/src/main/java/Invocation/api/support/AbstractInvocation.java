package Invocation.api.support;

import Invocation.api.Invocation;
import common.domain.RpcRequest;
import common.domain.RpcResponse;
import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;
import common.utils.InvokeParamUtil;
import config.ReferenceConfig;
import protocol.api.InvokeParam;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @Author: fnbory
 * @Date: 2019/10/7 15:59
 */
public abstract class AbstractInvocation implements Invocation {

    @Override
    public RpcResponse invoke(InvokeParam invokeParam, Function<RpcRequest, Future<RpcResponse>> requestProcessor) throws Rpcexception {
        RpcResponse response;
        ReferenceConfig referenceConfig = InvokeParamUtil.extractReferenceConfigFromInvokeParam(invokeParam);
        RpcRequest rpcRequest = InvokeParamUtil.extractRequestFromInvokeParam(invokeParam);
        try {
            response = doInvoke(rpcRequest, referenceConfig,requestProcessor);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Rpcexception(e, ERROR_ENUM.TRANSPORT_FAILURE, "transport异常");
        }
        return response;
    }

    protected abstract RpcResponse doInvoke(RpcRequest rpcRequest, ReferenceConfig referenceConfig,Function<RpcRequest, Future<RpcResponse>> requestProcessor) throws Throwable;

}
