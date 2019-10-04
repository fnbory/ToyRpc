package common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 23:35
 */
@Data
public class RpcRequest implements Serializable {

    private String requestId;

    private String interfaceName;

    private String methodName;






}
