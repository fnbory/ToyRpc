package protocol.api.support;

import common.domain.RpcResponse;
import common.exception.Rpcexception;
import protocol.api.InvokeParam;
import protocol.api.Invoker;
import registry.api.ServiceURL;

/**
 * @Author: fnbory
 * @Date: 2019/10/7 15:12
 */
public abstract class InvokerDelegate extends AbstractInvoker {

    private Invoker delegate;


    public InvokerDelegate(Invoker delegate) {
        this.delegate = delegate;
    }

    public Invoker getDelegate() {
        return delegate;
    }

    @Override
    public int hashCode() {
        return delegate.interfaceClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Invoker) {
            Invoker rhs = (Invoker) obj;
            return delegate.interfaceClass().equals(rhs.interfaceClass());
        }
        return false;
    }


    @Override
    public boolean isAvailable() {
        return delegate.isAvailable();
    }

    @Override
    public Class interfaceClass() {
        return delegate.interfaceClass();
    }

    @Override
    public String getInterfaceName() {
        return delegate.getInterfaceName();
    }

    @Override
    public ServiceURL getServiceURL() {
        return delegate.getServiceURL();
    }

    @Override
    public abstract RpcResponse invoke(InvokeParam invokeParam) throws Rpcexception;

}
