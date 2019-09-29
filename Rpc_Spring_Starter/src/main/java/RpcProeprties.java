import config.ClusterConfig;
import config.RegistryConfig;
import lombok.Data;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 20:17
 */

@Data
public class RpcProeprties {

    private RegistryConfig registryConfig;

    private ClusterConfig clusterConfig;
}
