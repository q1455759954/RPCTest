package server;

import net.sf.json.JSONObject;
import server.api.UserServceImpl;
import server.api.UserService;
import server.entity.AppointmentInfo;
import server.es.EsHandler;
import server.es.EsManagerImpl;
import server.es.QueryInfo;
import server.es.QueryKey;

import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args){
//        UserService userService = new UserServceImpl();

//        QueryInfo queryInfo = new QueryInfo();
//        Map<String, String> requestMap = new HashMap<>();
//        QueryKey key = new QueryKey();
//        Map<String,String> a = new HashMap<>();
//        a.put("nickname","果");
//        key.setLike(JSONObject.fromObject(a));
//        requestMap.put("query", JSONObject.fromObject(key).toString());
//        queryInfo.setRequestMap(requestMap);
        EsHandler esHandler = new EsHandler();
        EsManagerImpl esManager = new EsManagerImpl();
        esManager.delete("null", AppointmentInfo.class);



//        userService.getUserInfo(queryInfo);

    }

}
