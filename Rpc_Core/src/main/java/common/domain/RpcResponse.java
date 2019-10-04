package common.domain;

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
}
