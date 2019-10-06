package registry.api;

/**
 * @Author: fnbory
 * @Date: 2019/10/6 15:44
 */
@FunctionalInterface
public interface ServiceURLAddOrUpdateCallback {
    void addOrUpdate(ServiceURL serviceURL);
}
