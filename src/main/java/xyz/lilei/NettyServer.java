package xyz.lilei;

import com.sun.org.apache.bcel.internal.generic.NEW;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xyz.lilei.server.ServerInit;
import xyz.lilei.vo.NettyConstant;

/**
 * @ClassName NettyServer
 * @Description TODO 服务端主入口
 * @Author lilei
 * @Date 19/08/2019 07:29
 * @Version 1.0
 **/
public class NettyServer {

    private static final Log LOG = LogFactory.getLog(NettyServer.class);

    public void bind() throws Exception{
        // 配置服务端的Nio线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ServerInit());

        // 绑定端口, 同步等待成功
        b.bind(NettyConstant.REMOTE_PORT).sync();
        LOG.info("Netty server start : "
                + (NettyConstant.REMOTE_IP + " : " + NettyConstant.REMOTE_PORT));
    }

    public static void main(String[] args) {
        try {
            new NettyServer().bind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
