package registry.support;

import common.Constant.CharSetConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.StringUtils;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 14:35
 */
@Slf4j
public class ZkSupport {

    private ZooKeeper zookeeper=null;

    private static final int ZK_SESSION_TIMEOUT = 5000;

    private CountDownLatch connectedSemaphore=new CountDownLatch(1);

    public void connect(String address) {
        try {
            zookeeper=new ZooKeeper(address,ZK_SESSION_TIMEOUT,(WatchedEvent event)-> {
                // 获取事件状态
                Watcher.Event.KeeperState keeperState=event.getState();
                Watcher.Event.EventType eventType = event.getType();
                //如果是建立连接
                if (Watcher.Event.KeeperState.SyncConnected == keeperState) {
                    if (Watcher.Event.EventType.None == eventType) {
                        //如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
                        connectedSemaphore.countDown();
                        log.info("ZK建立连接");
                    }
                }
            });
            log.info("开始连结服务器");
            connectedSemaphore.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPathIfAbsent(String path, CreateMode createMode)throws KeeperException ,InterruptedException{
        String[] split=path.split("/");
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<split.length;i++){
            if(StringUtils.hasText(split[i])){
                sb.append(split[i]);
                Stat s=zookeeper.exists(sb.toString(),false);
                if (s == null) {
                    zookeeper.create(sb.toString(), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                }
            }
            if (i < split.length - 1) {
                sb.append("/");
            }
        }
    }

    public void createNodeIfAbsent(String data, String path) {
        try {
            byte[] bytes = data.getBytes(CharSetConst.UTF_8);
            zookeeper.create(path + "/" + data, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            log.info("建立数据节点 ({} => {})", path, data);
        } catch (KeeperException e) {
            if (e instanceof KeeperException.NodeExistsException) {
                //throw new RPCException(ErrorEnum.REGISTRY_ERROR,"ZK路径 {} 已经存在 : {},建议重启解决", path, data);
            } else {
                e.printStackTrace();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        try {
            this.zookeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
