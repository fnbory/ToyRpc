package common.domain;

import io.netty.util.Recycler;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: fnbory
 * @Date: 2019/10/4 16:50
 */
@Data
public class RpcResponse implements Serializable {

    private String requestId;

    private Object result;

    private Throwable cause;

    private Recycler.Handle handle;

    public RpcResponse(Recycler.Handle handle) {
        this.handle=handle;
    }

    public void recycle(){
        requestId=null;
        result=null;
        cause=null;
        handle.recycle(this);
    }

    public boolean hasError() {
        return cause!=null;
    }
}
