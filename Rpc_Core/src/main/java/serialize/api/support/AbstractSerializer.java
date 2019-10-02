package serialize.api.support;

import serialize.api.Serializer;

/**
 * @Author: fnbory
 * @Date: 2019/10/2 17:51
 */
public class AbstractSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        return null;
    }
}
