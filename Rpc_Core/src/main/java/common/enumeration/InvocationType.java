package common.enumeration;

import Invocation.Sync.SyncInvocation;
import Invocation.api.Invocation;
import common.utils.InvokeParamUtil;
import config.ReferenceConfig;
import protocol.api.InvokeParam;

/**
 * @Author: fnbory
 * @Date: 2019/10/7 15:56
 */
public enum InvocationType  {
    SYNC(new SyncInvocation()),;
    private  Invocation invocation;

    InvocationType(Invocation invocation) {
        this.invocation = invocation;
    }

    public static Invocation get(InvokeParam invokeParam){
        ReferenceConfig referenceConfig = InvokeParamUtil.extractReferenceConfigFromInvokeParam(invokeParam);
        if (referenceConfig.isAsync()) {
            return ASYNC.invocation;
        } else if (referenceConfig.isCallback()) {
            return CALLBACK.invocation;
        } else if (referenceConfig.isOneWay()) {
            return ONEWAY.invocation;
        } else {
            return SYNC.invocation;
        }
    }
}
