package common.enumeration;

import serialize.api.Serializer;
import serialize.hessian.HessianSerializer;
import serialize.jdk.JdkSerializer;
import serialize.json.JsonSerializer;
import serialize.protostuff.ProtostuffSerializer;

/**
 * @Author: fnbory
 * @Date: 2019/10/2 17:48
 */
public enum SerializerType implements  ExtentionBaseType {

    PROTOSTUFF(new ProtostuffSerializer()),
    JDK(new JdkSerializer()),
    HESSIAN(new HessianSerializer()),
    JSON(new JsonSerializer());

    private Serializer serializer;

    SerializerType(Serializer serializer){
        this.serializer=serializer;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    @Override
    public Object getInstance() {
        return null;
    }
}
