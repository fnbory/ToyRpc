package serialize.json;


import common.exception.Rpcexception;
import serialize.api.Serializer;

/**
 * @author sinjinsong
 * @date 2018/8/23
 */
public class JsonSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) throws Rpcexception {
        return JSONObject.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) throws RPCException {
        return JSONObject.parseObject(data, cls);
    }
}
