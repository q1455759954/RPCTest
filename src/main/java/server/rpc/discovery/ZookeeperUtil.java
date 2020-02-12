package server.rpc.discovery;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ZookeeperUtil {

    //zookeeper连接超时时间
    private static final int SESSION_TIMEOUT = 5000;
    //在zookeeper注册的根节点
    private static final String ROOT = "/rpc";

    private static ZooKeeper zooKeeper = null;

    /**
     * @param ip zookeeper服务的IP地址
     * @param port 端口号 连接zookeeper服务
     * 连接zookeeper
     */
    public static void connect(String ip, int port) {
        try {
            zooKeeper = new ZooKeeper(ip + ":" + port, SESSION_TIMEOUT, null);
        } catch (IOException e) {
            System.out.println("zookeeper连接失败");
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * @param ip provider服务的ip
     * 在zookeeper中注册暴漏的接口
     */

    public static void register(String ip,int port) {

        if (zooKeeper==null){
            throw new RuntimeException("zookeeper未连接");
        }
        try {
            List<String> interfaceList = ScanUtil.interfaceList;
            if (interfaceList.size() == 0) {
                System.out.println("没有可暴露的接口");
                return;
            }
            //如果根节点不存在，创建永久（PERSISTENT）根节点，临时节点下不能创建子节点
            if (zooKeeper.exists(ROOT, true) == null) {
                zooKeeper.create(ROOT, "rpc".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            for (String intf : interfaceList){
                String path = ROOT + "/" + intf;
                Stat exists = zooKeeper.exists(path,true);
                //节点不存在,创建临时（EPHEMERAL）节点
                if (exists==null){
                    zooKeeper.create(ROOT+"/"+intf,(ip + ":" + port).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                }else {
                    zooKeeper.setData(ROOT+ "/" + intf, (ip + ":" + port).getBytes(), -1);
                }
                System.out.println("zookeeper创建节点:" + intf + "成功");
            }
        }catch (Exception e) {
            System.out.println("zookeeper创建节点失败");
            e.printStackTrace();
            System.exit(0);
        }

    }

}
