package xyz.lilei.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xyz.lilei.vo.MessageType;
import xyz.lilei.vo.MyHeader;
import xyz.lilei.vo.MyMessage;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName LoginAuthRespHandler
 * @Description TODO
 * @Author lilei
 * @Date 19/08/2019 07:26
 * @Version 1.0
 **/
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

    private final static Log LOG = LogFactory.getLog(LoginAuthRespHandler.class);
    //用以检查用户是否重复登录的缓存
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();
    //用户登录的白名单
    private String[] whiteList = { "127.0.0.1"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessage message = (MyMessage) msg;

        // 如果是握手请求消息, 处理, 其它消息透传
        if (message.getMyHeader() != null && message.getMyHeader().getType()
         == MessageType.LOGIN_REQ.value()){
            String nodeIndex = ctx.channel().remoteAddress().toString();
            MyMessage loginResp = null;
            // 重复登陆, 拒绝
            if (nodeCheck.containsKey(nodeIndex))
                loginResp = buildResponse((byte)-1);
            else {
                // 检查用户是否在白名单中, 有则允许登陆, 并写入缓存
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOk = false;
                for (String WTP : whiteList) {
                    if (WTP.equals(ip)){
                        isOk = true;
                        break;
                    }
                }
                loginResp = isOk ? buildResponse((byte) 0):buildResponse((byte) -1);
                if (isOk) nodeCheck.put(nodeIndex, true);
            }
            LOG.info("The login response is : " + loginResp
                    + " body [" + loginResp.getBody() + "]");
            ctx.writeAndFlush(loginResp);
            ReferenceCountUtil.release(msg);
        }else ctx.fireChannelRead(msg);
    }

    private MyMessage buildResponse(byte result) {
        MyMessage message = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setType(MessageType.LOGIN_RESP.value());
        message.setMyHeader(myHeader);
        message.setBody(result);
        return message;
    }

    // 客户端突然断线,清除本地缓存

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 删除缓存
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
