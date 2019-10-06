package registry.api;

import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/10/6 14:51
 */
@FunctionalInterface
public interface ServiceURLRemovalCallback {
    void  removeNotExisted(List<ServiceURL> newAddresses);
}
