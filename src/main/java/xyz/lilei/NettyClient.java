package xyz.lilei;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xyz.lilei.client.ClientInit;
import xyz.lilei.vo.MessageType;
import xyz.lilei.vo.MyHeader;
import xyz.lilei.vo.MyMessage;
import xyz.lilei.vo.NettyConstant;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName KryoDecoder
 * @Description TODO Netty客户端的主入口
 * @Author lilei
 * @Date 19/08/2019 07:24
 * @Version 1.0
 **/
public class NettyClient implements Runnable{

    private static final Log LOG = LogFactory.getLog(NettyClient.class);

    private ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(1);
    private Channel channel;
    private EventLoopGroup group = new NioEventLoopGroup();

    // 是否用户主动关闭连接的标志
    private volatile boolean userClose = false;
    // 连接是否成功关闭的标志值
    private volatile boolean connected = false;

    private AtomicInteger loginRetryCount;

    public boolean isConnected(){
        return connected;
    }

    public void connect(final int port, final String host) throws Exception{
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientInit());
            // 发起异步连接操作
            ChannelFuture future = b.connect(new InetSocketAddress(host, port)).sync();
            channel = future.sync().channel();
            // 连接成功后通知等待线程, 连接已经建立
            synchronized (this){
                this.connected = true;
                this.notifyAll();
            }

            future.channel().closeFuture().sync();
        } finally {
            if (!userClose){ //非用户主动关闭进行重连
                System.out.println("发现异常，可能发生了服务器异常或网络问题，" +
                        "准备进行重连.....");
                //再次发起重连操作
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            try {
                                connect(NettyConstant.REMOTE_PORT
                                        , NettyConstant.REMOTE_IP);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }else {
                channel = null;
                group.shutdownGracefully().sync();
                synchronized (this){
                    this.connected = false;
                    this.notifyAll();
                }
            }

        }
    }

    @Override
    public void run() {
        try {
            connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTE_IP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 测试NettyClient
    public static void main(String[] args) throws Exception {
        NettyClient nettyClient = new NettyClient();
        nettyClient.connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTE_IP);
    }

    public void send(String message){
        if (channel == null||!channel.isActive()){
            throw new IllegalStateException("和服务器还未未建立起有效连接！，" +
                    "请稍后再试！！");
        }
        MyMessage msg = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setType(MessageType.SERVICE_REQ.value());
        msg.setMyHeader(myHeader);
        msg.setBody(message);
        channel.writeAndFlush(msg);
    }

    public void close(){
        userClose = true;
        channel.close();
    }
}
