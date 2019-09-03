package Server.netty;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: fnbory
 * @Date: 2019/8/22 11:15
 */
public class ChannelMap {

    private static Map<String, SocketChannel> channelmap=new HashMap<>();

    public static void put(String info,SocketChannel channel){
        channelmap.put(info,channel);
    }
    public static SocketChannel get(String info){
        return map.get(info);
    }
}
