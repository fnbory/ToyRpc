package Server.netty.serilize;

import Server.spring.serialization.Iserialization;
import Server.spring.serialization.Request;
import Server.spring.serialization.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author: fnbory
 * @Date: 2019/8/22 15:07
 */
public class MessageEncoder extends MessageToByteEncoder {

    private Iserialization iserialization;

    public  MessageEncoder(Iserialization iserialization){
        this.iserialization=iserialization;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byte[] bytes=null;
        if(o instanceof Request){
            Request request=(Request) o;
            bytes=iserialization.serialize(request);
            byteBuf.writeInt(1);
        }
        if(o instanceof Response) {
            Response response = (Response) o;
            bytes=iserialization.serialize(response);
            byteBuf.writeInt(2);
        }
        int datalength=bytes.length;
        byteBuf.writeInt(datalength);
        byteBuf.writeBytes(bytes);
        //下边这一行是强制写入并且刷新，如果这么写，在某些版本会抛出引用异常，因为每一次writeAndFlush
        //之后都会减少一次引用，而netty最后会自动帮我们减少一次引用
//        if (log.isInfoEnabled()) {
//            log.info("执行rpc:{}", obj);
//        }
        //write之后又一个减cnf的操作
        //ctx.writeAndFlush(byteBuf);
    }
}
