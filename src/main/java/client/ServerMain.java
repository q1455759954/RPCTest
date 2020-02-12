package client;

import client.api.UserService;
import client.rpc.discovery.ZookeeperUtil;
import client.rpc.proxy.InterfaceProxy;
import com.alibaba.fastjson.JSONObject;
import server.entity.UserInfo;
import server.entity.SearchInfo;
import server.es.QueryInfo;
import server.es.QueryKey;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ServerMain {


    public static void main(String[] args) throws InterruptedException {

        //连接到zookeeper
        ZookeeperUtil.connect("188.131.218.191", 2181);
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(11);
        for (int i=0;i<1;i++){
            executorService.execute(new Task(latch));
        }
        //主线程睡眠10s，等待50个线程就绪
        Thread.sleep(5000);
        System.out.println("----并发开始-----");
        //计数器减一到达零，线程开始执行
        latch.countDown();

    }


}
//并发测试
class Task implements Runnable {

    private final CountDownLatch latch;

    public Task(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        //计数器减一
        latch.countDown();
        try {
            //线程阻塞，当计数器等于零时，唤醒该线程
            latch.await();
            System.out.println("qqqqqqqqqqqqqq");
            //动态代理绑定
            UserService userService = InterfaceProxy.newInterfaceProxy(UserService.class);
            //执行结果

            QueryInfo queryInfo = new QueryInfo();
            Map<String, String> requestMap = new HashMap<>();
            QueryKey key = new QueryKey();
            Map<String,String> a = new HashMap<>();
            a.put("nickname","果");
            key.setLike(net.sf.json.JSONObject.fromObject(a));
            requestMap.put("query", net.sf.json.JSONObject.fromObject(key).toString());
            queryInfo.setRequestMap(requestMap);

            SearchInfo searchInfo = userService.getUserInfo(net.sf.json.JSONObject.fromObject(queryInfo).toString());
            for (UserInfo userInfo : searchInfo.getUserInfoList()){
                System.out.println(userInfo.toString());
            }












            UserInfo userInfo = new UserInfo();
            userInfo.setIntroduce("我是一个傻子");
            JSONObject json = (JSONObject)JSONObject.toJSON(userInfo);
            int i=0;
            while (1==1){
            Thread.sleep(2000);
            if (i%2==0){
                userService.LogToHive(json.toString());
                String b ="\"\\{.*\\}\"";
            }else{
//                logger.fatal("now is "+i);
            }
            i++;
        }


//            String[] a = userService.getRecommends("2","2");
//            for (String aa: a){
//                System.out.println(aa);
//            }

//            System.out.println(userService.sayHello("Tom"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}