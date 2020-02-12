package client.rpc.proxy;

import client.rpc.discovery.ZookeeperUtil;
import client.rpc.config.GlobalConfig;
import client.rpc.netty.client.ClientHandler;
import client.rpc.netty.client.NettyClient;
import client.rpc.netty.protocol.RpcFuture;
import client.rpc.netty.protocol.RpcRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import server.entity.SearchInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class InterfaceProxy implements InvocationHandler{

    private static Class<?> clazz;

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //根据接口，从zookeeper获取到服务端的ip+port
        String ipAndPort = ZookeeperUtil.getDataFromServer("server.api.UserService");
        System.out.println("wwwwwwwwwwwwwwwww");
        System.out.println(ipAndPort);
        if (ipAndPort == null || ipAndPort.length() == 0) {
            return null;
        }
        String[] content = ipAndPort.split(":");
        //client启动，连接到server
        ClientHandler handler = NettyClient.startNettyClient(content[0], Integer.valueOf(content[1]));
        //组装request
        RpcRequest request = new RpcRequest()
                .setId(UUID.randomUUID().toString())
                .setClassName("server.api.UserService")
                .setMethodName(method.getName())
                .setParameterTypes(method.getParameterTypes())
                .setParameters(args);
        //获取server端的响应结果
        RpcFuture rpcFuture = handler.sendRequest(request, GlobalConfig.getChannelMap().get(content[0] + ":" + content[1]));
        if (method.getName().equals("getRecommends")){
            return JSON.parseObject(rpcFuture.get().toString(),String[].class);
        }else {
            return JSON.parseObject(rpcFuture.get().toString(), SearchInfo.class);
        }
    }

    //动态代理绑定

    public static <T> T newInterfaceProxy(Class<T> intf){
        ClassLoader loader = intf.getClassLoader();
        Class<?>[] interfaces = new Class[]{intf};
        InterfaceProxy proxy = new InterfaceProxy();
        clazz = intf;
        return (T) Proxy.newProxyInstance(loader,interfaces,proxy);
    }

}
