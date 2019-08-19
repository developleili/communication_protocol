package xyz.lilei.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import xyz.lilei.kryocodec.KryoDecoder;
import xyz.lilei.kryocodec.KryoEncoder;

/**
 * @ClassName KryoDecoder
 * @Description TODO
 * @Author lilei
 * @Date 19/08/2019 07:24
 * @Version 1.0
 **/
public class ServerInit extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        /*Netty提供的日志打印Handler，可以展示发送接收出去的字节*/
        //ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
        /*剥离接收到的消息的长度字段，拿到实际的消息报文的字节数组*/
        sc.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
            65535,0,2,0,2
        )).addLast("frameEncoder", new LengthFieldPrepender(2)) // 给发送得消息增加长度字段
        .addLast("MessageDecoder",new KryoDecoder()) // 反序列化, 将字节数组转换为消息实体
        .addLast("MessageEncoder",new KryoEncoder()) // 序列化, 将消息实体转换为字节数组准备进行网络传输
        .addLast("readTimeOutHandler", new ReadTimeoutHandler(50)) // 增加超时检测机制
        .addLast("LoginAuthResp",new LoginAuthRespHandler()) // 登陆应答
        .addLast("HeartBeatHandler", new HeartBeatRespHandler()) // 心跳应答
        .addLast("ServerBusiHandler", new ServerBusiHandler()); // 服务端业务处理
    }
}
