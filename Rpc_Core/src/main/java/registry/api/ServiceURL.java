package registry.api;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: fnbory
 * @Date: 2019/10/6 15:12
 */
@ToString
@EqualsAndHashCode(of = {"addressx"})
@Slf4j
public class ServiceURL {
    private String address;
    private Map<Key, List<String>> params=new HashMap<>();

    public static ServiceURL parse(String data) {
        ServiceURL serviceURL=new ServiceURL();
        String[] urlSlices = data.split("\\?");
        serviceURL.address = urlSlices[0];
        //解析URL参数
        if (urlSlices.length > 1) {
            String params = urlSlices[1];
            String[] urlParams = params.split("&");
            for (String param : urlParams) {
                String[] kv = param.split("=");
                String key = kv[0];
                Key keyEnum = Key.valueOf(key.toUpperCase());
                if (keyEnum != null) {
                    String[] values = kv[1].split(",");
                    serviceURL.params.put(keyEnum, Arrays.asList(values));
                } else {
                    log.error("key {} 不存在 ", key);
                }
            }
        }
        return serviceURL;
    }

    public String getAddress() {
        return address;
    }

    public enum Key{

    }

}
