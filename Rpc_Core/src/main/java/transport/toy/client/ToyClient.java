package transport.toy.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import transport.api.constant.FrameConstant;
import transport.api.converter.ClientMessageConverter;
import transport.api.support.netty.AbstractNettyClient;
import transport.toy.codec.ToyDecoder;
import transport.toy.codec.ToyEncoder;
import transport.toy.constant.ToyConstant;

/**
 * @Author: fnbory
 * @Date: 2019/10/7 14:32
 */
@Slf4j
public class ToyClient extends AbstractNettyClient {


    @Override
    protected ChannelInitializer initPipeline() {
        log.info("ToyClient initPipeline...");
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast("IdleStateHandler", new IdleStateHandler(0, ToyConstant.HEART_BEAT_TIME_OUT, 0))
                        // ByteBuf -> Message
                        .addLast("LengthFieldPrepender", new LengthFieldPrepender(FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT))
                        // Message -> ByteBuf
                        .addLast("ToyEncoder", new ToyEncoder(getGlobalConfig().getSerializer()))
                        // ByteBuf -> Message
                        .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(FrameConstant.MAX_FRAME_LENGTH, FrameConstant.LENGTH_FIELD_OFFSET, FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT, FrameConstant.INITIAL_BYTES_TO_STRIP))
                        // Message -> Message
                        .addLast("ToyDecoder", new ToyDecoder(getGlobalConfig().getSerializer()))

                        .addLast("ToyClientHandler", new ToyClientHandler(ToyClient.this));
            }
        };
    }

    @Override
    protected ClientMessageConverter initConverter() {
        return ClientMessageConverter.DEFAULT_IMPL;
    }
}
