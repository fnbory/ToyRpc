package config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 16:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GlobalConfig {

    private ClusterConfig clusterConfig;

    private RegistryConfig registryConfig;

    private ApplicationConfig applicationConfig;

    private ProtocolConfig protocolConfig;


}
