import config.ApplicationConfig;
import config.ClusterConfig;
import config.ProtocolConfig;
import config.RegistryConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 20:17
 */

@Data
@ConfigurationProperties(prefix = "rpc")
public class RpcProeprties {

    private RegistryConfig registryConfig;

    private ClusterConfig clusterConfig;

    private ProtocolConfig protocolConfig;

    private ApplicationConfig applicationConfig;
}
