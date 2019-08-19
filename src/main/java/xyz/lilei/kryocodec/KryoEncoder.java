package xyz.lilei.kryocodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import xyz.lilei.vo.MyMessage;

/**
 * @ClassName KryoDecoder
 * @Description TODO
 * @Author lilei
 * @Date 19/08/2019 07:24
 * @Version 1.0
 **/
public class KryoEncoder extends MessageToByteEncoder<MyMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MyMessage myMessage, ByteBuf byteBuf) throws Exception {
        KryoSerializer.serialize(myMessage, byteBuf);
        channelHandlerContext.flush();
    }
}
