package transport.toy.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import transport.api.constant.FrameConstant;
import transport.api.converter.ServerMessageConverter;
import transport.api.support.netty.AbstractNettyServer;
import transport.toy.codec.ToyDecoder;
import transport.toy.codec.ToyEncoder;
import transport.toy.constant.ToyConstant;

/**
 * @Author: fnbory
 * @Date: 2019/10/3 19:11
 */
public class ToyServer extends AbstractNettyServer {


    @Override
    protected ServerMessageConverter initConverter() {

    }

    @Override
    protected ChannelInitializer initPipeline() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast("IdleStateHandler",new IdleStateHandler(ToyConstant.HEART_BEAT_TIME_OUT_MAX_TIME*ToyConstant.HEART_BEAT_TIME_OUT,0,0))
                        .addLast("LengthFieldPrepender",new LengthFieldPrepender(FrameConstant.LENGTH_FIELD_LENGTH,FrameConstant.LENGTH_ADJUSTMENT))
                        .addLast("ToyEncoder", new ToyEncoder(getGlobalConfig().getSerializer()))
                        .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(FrameConstant.MAX_FRAME_LENGTH,
                                FrameConstant.LENGTH_FIELD_OFFSET,
                                FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT, FrameConstant.INITIAL_BYTES_TO_STRIP))
                        .addLast("ToyDecoder", new ToyDecoder(getGlobalConfig().getSerializer()))
                        .addLast("ToyServerHandler", new ToyServerHandler(ToyServer.this));

            }
        };
    }
}
