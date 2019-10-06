package common.utils;

import common.domain.RpcRequest;
import config.ReferenceConfig;
import protocol.api.InvokeParam;
import protocol.api.support.RpcInvokeParam;

/**
 * @Author: fnbory
 * @Date: 2019/10/6 22:19
 */
public class InvokeParamUtil {

    public static RpcRequest extractRequestFromInvokeParam(InvokeParam invokeParam) {
        return  ((RpcInvokeParam)invokeParam).getRpcRequest();
    }

    public static ReferenceConfig extractReferenceConfigFromInvokeParam(InvokeParam invokeParam) {
        return  ((RpcInvokeParam)invokeParam).getReferenceConfig();
    }
}
