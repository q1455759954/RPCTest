package client.rpc.discovery;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class ZookeeperUtil {

    private static final int SESSION_TIMEOUT = 5000;
    private static final String ROOT = "/rpc";
    private static ZooKeeper zooKeeper = null;

    public static void connect(String ip,int port){
        try {
            zooKeeper = new ZooKeeper(ip+":"+port,SESSION_TIMEOUT,null);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("zookeeper连接失败");
            System.exit(0);
        }

    }

    public static String getDataFromServer(String api){
        try {
            String path = ROOT+"/"+api;
            Stat exists = zooKeeper.exists(path,true);
            if (exists!=null){
                byte[] data = zooKeeper.getData(ROOT+"/"+api,true,new Stat());
                String ipAndPost = new String(data);
                return ipAndPost;
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
