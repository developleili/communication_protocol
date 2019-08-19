package xyz.lilei.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xyz.lilei.server.LoginAuthRespHandler;
import xyz.lilei.vo.MessageType;
import xyz.lilei.vo.MyHeader;
import xyz.lilei.vo.MyMessage;

/**
 * @ClassName LoginAuthReqHandler
 * @Description TODO 客户端登陆请求handler
 * @Author lilei
 * @Date 19/08/2019 07:23
 * @Version 1.0
 **/
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {
    private final static Log LOG = LogFactory.getLog(LoginAuthRespHandler.class);

    // 建立连接后, 发起登陆请求


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessage message = (MyMessage) msg;
        // 如果是登陆请求处理
        if (message.getMyHeader() != null
                && message.getMyHeader().getType() == MessageType.LOGIN_REQ.value()){
            byte loginResult = (byte) message.getBody();
            if (loginResult != (byte) 0){
                // 握手失败, 关闭连接
                ctx.close();
            }else {
                LOG.info("Login is ok : " + message);
                ctx.fireChannelRead(msg);
            }
        }else ctx.fireChannelRead(msg);
    }

    private MyMessage buildLoginReq() {
        MyMessage message = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setType(MessageType.LOGIN_REQ.value());
        message.setMyHeader(myHeader);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
