package config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import protocol.api.Protocol;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 19:42
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ProtocolConfig {
    // 设定的协议名称
    private  String type;

    // 服务注册端口号
    private Integer port;

    public static final Integer DEFAULT_PORT = Integer.valueOf(8000);

    private Protocol protocolInstance;

    public int getPort() {
        if (port != null) {
            return port;
        }
        return DEFAULT_PORT;
    }

    public void close(){
        protocolInstance.close();
    }


}
