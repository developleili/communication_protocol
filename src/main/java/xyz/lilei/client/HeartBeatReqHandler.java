package xyz.lilei.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xyz.lilei.vo.MessageType;
import xyz.lilei.vo.MyHeader;
import xyz.lilei.vo.MyMessage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName HeartBeatReqHanler
 * @Description TODO
 * @Author lilei
 * @Date 19/08/2019 07:22
 * @Version 1.0
 **/
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
    private static final Log LOG = LogFactory.getLog(HeartBeatReqHandler.class);

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessage message = (MyMessage) msg;
        // 握手或者登陆成功, 主动发送心跳消息
        if (message.getMyHeader() != null &&
            message.getMyHeader().getType() == MessageType.LOGIN_RESP.value()){
            heartBeat = ctx.executor().scheduleAtFixedRate(
                    new HeartBeatReqHandler.heartBeatTask(ctx),0,5000, TimeUnit.MILLISECONDS);
        }
    }

    private class heartBeatTask implements Runnable  {
        private final ChannelHandlerContext ctx;
        // 心跳计数
        private final AtomicInteger heartBeatCount;

        public heartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
            this.heartBeatCount = new AtomicInteger(0);
        }

        @Override
        public void run() {
            MyMessage heartBeat = buildHeatBeat();
            LOG.info("Client send heart beat messsage to server : ---> "
                            + heartBeat);
            ctx.writeAndFlush(heartBeat);
        }

        private MyMessage buildHeatBeat() {
            MyMessage message = new MyMessage();
            MyHeader myHeader = new MyHeader();
            myHeader.setType(MessageType.HEARTBEAT_REQ.value());
            message.setMyHeader(myHeader);
            return message;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (heartBeat != null){
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}
