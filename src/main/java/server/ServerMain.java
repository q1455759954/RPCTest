package server;

import server.api.PredictTimeLine;
import server.rpc.discovery.ScanUtil;
import server.rpc.discovery.ZookeeperUtil;
import server.rpc.netty.server.NettyServer;

public class ServerMain {

    //server启动监听的端口
    public static final int serverPort = 8018;

    public static void main(String[] args) {

//        PredictTimeLine.getModel();

        //扫描暴露的接口
        ScanUtil.scanPath("server.api");
        //连接zookeeper
        ZookeeperUtil.connect("188.131.218.191", 2181);
        //向zookeeper注册暴露的接口
        ZookeeperUtil.register("127.0.0.1", serverPort);
        //启动server(此过程放在最后，启动服务后，此方法下的不再执行)
        new NettyServer().bind(serverPort);
    }

}
