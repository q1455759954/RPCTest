package server.rpc.discovery;

import javafx.scene.effect.Reflection;
import org.reflections.Reflections;
import server.rpc.annotation.RpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ScanUtil {


    //暴露服务的接口
    public static List<String> interfaceList = new ArrayList<>();
    //暴露接口和实现类的映射关系
    public static Map<String,Class<?>> map = new ConcurrentHashMap<>();

    //扫描类
    public static void scanPath(String path){
        Reflections reflections = new Reflections(path);
        //扫描带有@RpcService的类
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(RpcService.class);
        for (Class<?> clazz : annotated){
            RpcService rpcService = clazz.getAnnotation(RpcService.class);
            //暴露服务的接口
            interfaceList.add(rpcService.value().getName());
            //暴露接口和映射类的关系
            map.put(rpcService.value().getName(),clazz);
        }
    }


}
