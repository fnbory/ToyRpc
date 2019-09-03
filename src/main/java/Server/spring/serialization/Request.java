package Server.spring.serialization;

import Server.Provider;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @Author: fnbory
 * @Date: 2019/8/19 23:29
 */
@Getter
@Setter
@ToString
public class Request {

    private  String requestId;

    private String serviceName;

    private  String methodName;

    private Class<?>[] parameters;

    private Object[] args;

    private Boolean async;

    private Integer code=1;

    transient private String serialization;

    transient private Provider provider;

    transient private Method method;

    transient private Integer timeout;

    public static  Request buildRequest(Provider provider,Object[] args,Integer timeout,Method method){
        Request request=new Request();
        request.setRequestId(UUID.randomUUID().toString().replaceAll("-",""));
        request.setServiceName(provider.getServiceName());
        request.setMethodName(method.getName());
        request.setArgs(args);
        request.setParameters(method.getParameterTypes());
        request.setSerialization(provider.getSerialization());
        request.setProvider(provider);
        request.setTimeout(timeout);
        request.setMethod(method);
        return request;
    }
}
