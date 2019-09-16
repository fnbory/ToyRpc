package Server.zk;

import Server.Consumer;
import Server.Provider;
import Server.spring.proxy.ProviderSet;
import Server.utils.NetUtils;
import com.github.zkclient.ZkClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: fnbory
 * @Date: 2019/8/27 17:27
 */
@Slf4j
public class ZkRegister implements  Iregister{


    final static  String root_path="/fnbory";

    final static String root_provider="/provider";

    private final static  String root_consumer="/consumer";

    private final static  String line="/";

    private final static String split="#A#";

    private final static  Integer default_session_timeout=1000;

    private final static  Integer default_connection_timeout=10000;

    @Getter
    @Setter
    private ZkClient zkClient;

    @Getter
    @Setter
    private Integer session=default_session_timeout;

    @Getter
    @Setter
    private Integer connection=default_connection_timeout;



    public ZkRegister(String host){
        this(host,default_session_timeout,default_connection_timeout);
    }

    public ZkRegister(String host, Integer session, Integer connection) {
        assert host!=null;
        assert session>0;
        assert connection>0;
        zkClient=new ZkClient(host,session,connection);
        if(!zkClient.exists(root_path)){
            zkClient.createPersistent(root_path,"toyrpc root path".getBytes(Charset.forName("utf-8")));
        }
    }



    @Override
    public void registerService(List<Provider> providers) {
        assert zkClient!=null;
        providers.parallelStream().forEach(provider -> {
            String host=provider.getHost();
            Integer port=provider.getPort();
            String serviceName=provider.getServiceName();
            String version=provider.getVersion();
            Integer weight=provider.getWeight();
            String serverPath=root_path+"/"+serviceName+root_provider;
            if(!zkClient.exists(serverPath)){
                zkClient.createPersistent(serverPath,true);
            }
            String finalInfo = host + split + port + split + serviceName + split + version + split + weight + split + provider.getSerialization();
            String path=serverPath+"/"+finalInfo;
            if(!zkClient.exists(path)){
                log.info("注册服务:{}到ZooKeeper", serverPath);
                zkClient.createEphemeral(path);
            }
            else{
                 log.warn("已经被注册");
            }
        });
    }

    @Override
    public List<Provider> discover(String serviceName, String version) {
        assert zkClient!=null;
        String serverPath=root_path+"/"+serviceName+root_provider;
        List<String> children=null;
        children=zkClient.getChildren(serverPath);

        if(children==null||children.size()==0){
            throw  new RuntimeException("提供者列表为空");
        }
        List<Provider> collect=children.parallelStream().map(this::toProvider).collect(Collectors.toList());
        List<Provider> result=collect.stream().filter(provider ->
            provider.getVersion().equalsIgnoreCase(version)).collect(Collectors.toList());
        if(result==null|| result.size()==0){
            throw new RuntimeException("版本号:[" + version + "]的提供者列表为空");
        }
        return result;
    }

    private Provider toProvider(String child){
        assert child!=null;
        String[] info=child.split(ZkRegister.split);
        assert info.length==6;
        Provider provider=new Provider();
        provider.setHost(info[0]);
        provider.setPort(Integer.valueOf(info[1]));
        provider.setServiceName(info[2]);
        provider.setVersion(info[3]);
        provider.setWeight(NumberUtils.parseNumber(info[4], Integer.class));
        provider.setSerialization(info[5]);
        return provider;
    }

    @Override
    public void registerConsumer(Consumer consumer) {
        Assert.notNull(consumer,"消费者信息不能为空");
        String host = consumer.getHost();
        String version = consumer.getVersion();
        Integer timeout = consumer.getTimeout();
        String consumerPath = root_path + "/" + consumer.getServiceName() + root_consumer;
        if (!zkClient.exists(consumerPath)) {
            zkClient.createPersistent(consumerPath, true);
        }
        RuntimeMXBean runtimeMXBean= ManagementFactory.getRuntimeMXBean();
        Integer integer = Integer.valueOf(runtimeMXBean.getName().split("@")[0]);
        String finalInfo = host + split + consumer.getServiceName() + split + version + split + timeout + split + integer;
        String path = consumerPath + "/" + finalInfo;
        if (!zkClient.exists(path)) {
            zkClient.createEphemeral(path);
        }
    }

    /**
     *
     * @param service
     */

    @Override
    public void subscribe(String service) {
        Assert.notNull(this.zkClient, "zkClient不能为空");
        String finalPath = root_path + line + service + root_provider;
        this.zkClient.subscribeChildChanges(finalPath, (parent, children) -> {
            log.info("notify {} about subscribe server:{},provider list:{}", NetUtils.getLocalHost(), service, children);
            List<Provider> all = ProviderSet.getAll(service);
            List<Provider> collect = children.stream().map(this::toProvider).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(all) && all.equals(collect)) {
                log.debug("提供者没有变更");
            } else {
                ProviderSet.reset(service, collect);
            }
        });
    }


}
