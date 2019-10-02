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

    private Protocol protocolInstance;


}
