package xyz.lilei.kryocodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName KryoDecoder
 * @Description TODO 反序列化的Handler
 * @Author lilei
 * @Date 19/08/2019 07:24
 * @Version 1.0
 **/
public class KryoDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        Object obj = KryoSerializer.deserialize(byteBuf);
        list.add(obj);
    }
}
