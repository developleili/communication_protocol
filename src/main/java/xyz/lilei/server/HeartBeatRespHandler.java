package xyz.lilei.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xyz.lilei.vo.MessageType;
import xyz.lilei.vo.MyHeader;
import xyz.lilei.vo.MyMessage;

/**
 * @ClassName HeatBeatRespHandler
 * @Description TODO 心跳应答
 * @Author lilei
 * @Date 19/08/2019 07:26
 * @Version 1.0
 **/
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(HeartBeatRespHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessage message = (MyMessage) msg;
        // 返回心跳应答信息
        if(message.getMyHeader() != null
            && message.getMyHeader().getType() == MessageType.HEARTBEAT_REQ.value()){
//			LOG.info("Receive client heart beat message : ---> "
//				+ message);
            MyMessage heartBeat = buildHeatBeat();
            ctx.writeAndFlush(heartBeat);
            ReferenceCountUtil.release(msg);
        }else
            ctx.fireChannelRead(msg);
    }

    // 心跳应答报文
    private MyMessage buildHeatBeat() {
        MyMessage message = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setType(MessageType.HEARTBEAT_RESP.value());
        message.setMyHeader(myHeader);
        return message;
    }
}
