package client.rpc.netty.client;
/*
 * @author uv
 * @date 2018/10/12 20:54
 * NettyClient
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import client.rpc.config.GlobalConfig;
import client.rpc.netty.protocol.RpcDecoder;
import client.rpc.netty.protocol.RpcEncoder;
import client.rpc.netty.protocol.RpcRequest;
import client.rpc.netty.protocol.RpcResponse;

public class NettyClient {

    private final String host;
    private final int port;

    //连接服务端的端口号地址和端口号
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        final EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)  // 使用NioSocketChannel来作为连接用的channel类
            .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    System.out.println("正在连接中...");
                    ClientHandler handler = new ClientHandler();
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new RpcEncoder(RpcRequest.class)); //编码request
                    pipeline.addLast(new RpcDecoder(RpcResponse.class)); //解码response
                    pipeline.addLast(handler); //客户端处理类
                    GlobalConfig.getHandlerMap().put(host + ":" + port, handler);
                }
            });
        //发起异步连接请求，绑定连接端口和host信息
        final ChannelFuture future = b.connect(host, port).sync();

        future.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture arg0) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("连接服务器成功");
                } else {
                    System.out.println("连接服务器失败");
                    future.cause().printStackTrace();
                    group.shutdownGracefully(); //关闭线程组
                }
            }
        });

        //放入map中存储
        GlobalConfig.getChannelMap().put(host + ":" + port, future.channel());
    }

    //开启客户端，连接服务端
    public static synchronized ClientHandler startNettyClient(String host, int port) {
        try {
            //map中没有有对象，则客户端未启动
            if(!GlobalConfig.getHandlerMap().containsKey(host + ":" + port) || !GlobalConfig.getChannelMap().containsKey(host + ":" + port)) {
                NettyClient client = new NettyClient(host, port);
                //启动客户端
                client.start();
            }
            return GlobalConfig.getHandlerMap().get(host + ":" + port);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}
