package xyz.lilei;

import java.util.Scanner;

/**
 * @ClassName BusiClient
 * @Description TODO 模拟客户
 * @Author lilei
 * @Date 19/08/2019 07:28
 * @Version 1.0
 **/
public class BusiClient {
    public static void main(String[] args) throws InterruptedException {
        NettyClient nettyClient = new NettyClient();
        new Thread(nettyClient).start();
        while (!nettyClient.isConnected()){
            synchronized (nettyClient){
                nettyClient.wait();
            }
        }
        System.out.println("网络通信已准备好，可以进行业务操作了........");
        Scanner scanner = new Scanner(System.in);
        while (true){
            String msg = scanner.next();
            if (msg == null)
                continue;
            else if ("q".equals(msg.toLowerCase())){
                nettyClient.close();
                while (nettyClient.isConnected()){
                    synchronized (nettyClient){
                        nettyClient.wait();
                    }
                }
                scanner.close();
                System.exit(1);
            }else {
                nettyClient.send(msg);
            }
        }
    }
}
