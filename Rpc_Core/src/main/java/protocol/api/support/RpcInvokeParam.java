package protocol.api.support;

import common.domain.RpcRequest;
import config.ReferenceConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import protocol.api.InvokeParam;

/**
 * @Author: fnbory
 * @Date: 2019/10/5 16:46
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcInvokeParam implements InvokeParam {

    private ReferenceConfig referenceConfig;

    private RpcRequest rpcRequest;

    @Override
    public String getInterfaceName() {
        return rpcRequest.getInterfaceName();
    }

    @Override
    public String getMethodName() {
        return rpcRequest.getMethodName();
    }

    @Override
    public Class[] getParameterTypes() {
        return rpcRequest.getParameterTypes();
    }

    @Override
    public Object[] getParameters() {
        return rpcRequest.getParameters();
    }

    @Override
    public String requestId() {
        return rpcRequest.getRequestId();
    }

    @Override
    public String toString() {
        return "RPCInvokeParam{" +
                rpcRequest +
                '}';
    }
}
