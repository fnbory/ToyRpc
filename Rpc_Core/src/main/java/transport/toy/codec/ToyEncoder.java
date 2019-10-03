package transport.toy.codec;

import common.domain.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import serialize.api.Serializer;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 22:00
 */
@Slf4j
public class ToyEncoder extends MessageToByteEncoder {

    private Serializer serializer;

    public ToyEncoder(Serializer serializer){
        this.serializer=serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf out) throws Exception {
        Message message=new Message();
        out.writeByte(message.getType());
        if(message.getType()==Message.REQUEST){
            byte[] bytes=serializer.serialize(message.getRpcRequest());
            log.info("Message:{},序列化大小为:{}", message,bytes.length);
            out.writeBytes(bytes);
            //  message.getRequest().recycle();
        }
        else if(message.getType()==Message.RESPONSE){
            byte[] bytes = serializer.serialize(message.getResponse());
            log.info("Message:{},序列化大小为:{}", message,bytes.length);
            out.writeBytes(bytes);
          //  message.getResponse().recycle();
        }
    }
}
