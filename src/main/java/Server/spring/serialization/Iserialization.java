package Server.spring.serialization;

/**
 * @Author: fnbory
 * @Date: 2019/8/23 11:46
 */
public interface Iserialization {

    <T> byte[] serialize(T obj);

    <T> T deSerialize(byte[] bytes,Class<T> tClass);

}
