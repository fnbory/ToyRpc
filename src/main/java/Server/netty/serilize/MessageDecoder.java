package Server.netty.serilize;

import Server.spring.serialization.Iserialization;
import Server.spring.serialization.Request;
import Server.spring.serialization.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/8/22 15:07
 */
public class MessageDecoder extends ByteToMessageDecoder {

    private static final int HEAD_LENGTH = 8;//最小数据包头长度

    private Iserialization serialization;

    public MessageDecoder(Iserialization serialization){
        this.serialization=serialization;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes()<=HEAD_LENGTH){
            return;
        }
        byteBuf.markReaderIndex();
        int type=byteBuf.readInt();
        int dataLength=byteBuf.readInt();
        if(byteBuf.readableBytes()<dataLength){
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] dataArray=new byte[dataLength];
        byteBuf.readBytes(dataArray);
        Object object;
        if(type==1){
            object=serialization.deSerialize(dataArray, Request.class);
        }
        else {
            object=serialization.deSerialize(dataArray, Response.class);
        }
        list.add(object);
    }
}
