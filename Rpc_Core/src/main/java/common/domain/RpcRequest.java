package common.domain;

import common.utils.TypeUtil;
import io.netty.util.Recycler;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 23:35
 */
@Data
public class RpcRequest implements Serializable {

    private Recycler.Handle handle;

    private String requestId;

    private String interfaceName;

    private String methodName;

    private String[] parameterTypes;

    private Object[] parameters;

    public RpcRequest(Recycler.Handle handle){
        this.handle=handle;
    }

    public void setParamterTypes(Class[] parameterTypes){
        String[] paramTypes = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            paramTypes[i] = parameterTypes[i].getName();
        }
        this.parameterTypes = paramTypes;
    }

    public void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class[] getParameterTypes(){
        Class[] parameterTypeClasses=new Class[parameterTypes.length];
        try {
            for(int i=0;i<parameterTypes.length;i++){
                if(TypeUtil.isPrimitive(parameterTypes[i])){
                    parameterTypeClasses[i] = TypeUtil.map(parameterTypes[i]);
                }
                else {
                    parameterTypeClasses[i] = Class.forName(parameterTypes[i]);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return parameterTypeClasses;
    }





}
