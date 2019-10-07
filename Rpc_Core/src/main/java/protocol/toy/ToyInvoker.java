package protocol.toy;

import common.domain.RpcRequest;
import common.domain.RpcResponse;
import protocol.api.support.AbstractRemoteInvoker;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @Author: fnbory
 * @Date: 2019/10/6 17:27
 */
public class ToyInvoker extends AbstractRemoteInvoker {

    @Override
    protected Function<RpcRequest, Future<RpcResponse>> getProcessor() {
        return rpcRequest -> getClient().submit(rpcRequest);
    }
}
