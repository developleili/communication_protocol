package xyz.lilei.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xyz.lilei.vo.MyMessage;

/**
 * @ClassName KryoDecoder
 * @Description TODO
 * @Author lilei
 * @Date 19/08/2019 07:24
 * @Version 1.0
 **/
public class ServerBusiHandler  extends SimpleChannelInboundHandler<MyMessage> {
    private static final Log LOG = LogFactory.getLog(ServerBusiHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MyMessage myMessage) throws Exception {
        LOG.info(myMessage);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.info(ctx.channel().remoteAddress()+" 主动断开了连接!");
    }
}
