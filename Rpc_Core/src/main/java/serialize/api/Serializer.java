package serialize.api;

/**
 * @Author: fnbory
 * @Date: 2019/10/2 14:48
 */
public interface Serializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data,Class<T> cls);
}
