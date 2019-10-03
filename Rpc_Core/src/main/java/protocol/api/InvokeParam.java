package protocol.api;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 16:49
 */
public interface InvokeParam {

    String getMethodName();

    Class[] getParameterTypes();

    Object[] getParameters();
}
