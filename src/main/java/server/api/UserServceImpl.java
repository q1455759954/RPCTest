package server.api;
/*
 * @author uv
 * @date 2018/10/12 8:56
 * 接口实现类
 */


import com.alibaba.fastjson.JSON;
import server.bayes.Bayes;
import server.entity.*;
import org.apache.log4j.Logger;
import server.es.EsManagerImpl;
import server.es.IEsManager;
import server.es.QueryInfo;
import server.rpc.annotation.RpcService;

import java.util.ArrayList;
import java.util.List;

@RpcService(UserService.class)
public class UserServceImpl implements UserService {

    public static Bayes bayes = Bayes.getInstance();
    private static IEsManager esManager = new EsManagerImpl();
    private static Logger logger=Logger.getLogger(UserService.class.getName());

    @Override
    public String[] getRecommends(String uId, String since_id) {
        return PredictTimeLine.recommend(Integer.parseInt(uId),Integer.parseInt(since_id));
    }

    @Override
    public void LogToHive(String json) {
        logger.fatal(json);
    }

    @Override
    public SearchInfo getUserInfo(String queryInfo) {
        QueryInfo query = JSON.parseObject(queryInfo,QueryInfo.class);
        SearchInfo searchInfo = new SearchInfo();
        List<UserInfo> userInfoList = esManager.getList(query,UserInfo.class, "id").getData();
        searchInfo.setUserInfoList(userInfoList);
        return searchInfo;
    }

    @Override
    public SearchInfo getStatusInfo(String queryInfo) {
        QueryInfo query = JSON.parseObject(queryInfo,QueryInfo.class);
        SearchInfo searchInfo = new SearchInfo();
        List<StatusContent> contents = esManager.getList(query,StatusContent.class, "id").getData();
        searchInfo.setContentList(contents);
        return searchInfo;
    }

    @Override
    public void addUser(String userInfo) {
        UserInfo u = JSON.parseObject(userInfo,UserInfo.class);
        esManager.save(userInfo);

    }

    @Override
    public void updateUser(String userInfo) {
        UserInfo u = JSON.parseObject(userInfo,UserInfo.class);
        esManager.update(u);

    }

    @Override
    public void addStatusContent(String statusContent) {
        StatusContent s = JSON.parseObject(statusContent,StatusContent.class);
        esManager.save(s);
    }

    @Override
    public void addAppointment(String appointment) {
        AppointmentInfo a = JSON.parseObject(appointment,AppointmentInfo.class);
        esManager.save(a);
    }

    @Override
    public String classify(String text) {
        return bayes.classify(text);
    }


    @Override
    public void addCommodity(String info) {
        Commodity s = JSON.parseObject(info,Commodity.class);
        esManager.save(s);
    }

    @Override
    public void addWorkInfo(String info) {
        WorkInfo s = JSON.parseObject(info,WorkInfo.class);
        esManager.save(s);
    }

    @Override
    public SearchInfo getAppointmentInfo(String queryInfo) {
        QueryInfo query = JSON.parseObject(queryInfo,QueryInfo.class);
        SearchInfo searchInfo = new SearchInfo();
        List<AppointmentInfo> contents = esManager.getList(query,AppointmentInfo.class, "id").getData();
        searchInfo.setAppointmentInfoList(contents);
        return searchInfo;
    }

    @Override
    public SearchInfo getCommodityInfo(String queryInfo) {
        QueryInfo query = JSON.parseObject(queryInfo,QueryInfo.class);
        SearchInfo searchInfo = new SearchInfo();
        List<Commodity> commodities  =  esManager.getList(query,Commodity.class,"id").getData();
        List<LifeInfo> lifeInfos = new ArrayList<>();

        for (Commodity commodity: commodities){
            LifeInfo lifeInfo = new LifeInfo();
            lifeInfo.setCommodity(commodity);
            lifeInfos.add(lifeInfo);
        }
        searchInfo.setCommodityInfo(lifeInfos);
        return searchInfo;
    }

    @Override
    public SearchInfo getWorkInfo(String queryInfo) {
        QueryInfo query = JSON.parseObject(queryInfo,QueryInfo.class);
        SearchInfo searchInfo = new SearchInfo();
        List<WorkInfo> workInfos  =  esManager.getList(query,WorkInfo.class,"id").getData();
        List<LifeInfo> lifeInfos = new ArrayList<>();

        for (WorkInfo workInfo: workInfos){
            LifeInfo lifeInfo = new LifeInfo();
            lifeInfo.setWorkInfo(workInfo);
            lifeInfos.add(lifeInfo);
        }
        searchInfo.setWorkInfo(lifeInfos);
        return searchInfo;
    }

    /**
     * 下架
     * @param row_key
     */
    @Override
    public void updateAppointment(String row_key) {
        //先得到
        AppointmentInfo appointmentInfo  = esManager.get(row_key,AppointmentInfo.class);
        appointmentInfo.setState(true);
        esManager.update(appointmentInfo);
    }

    @Override
    public void updateCommodity(String row_key) {
        //先得到
        Commodity commodity  = esManager.get(row_key,Commodity.class);
        commodity.setState(true);
        esManager.update(commodity);
    }

    @Override
    public void updateWorkInfo(String row_key) {
        //先得到
        WorkInfo workInfo  = esManager.get(row_key,WorkInfo.class);
        workInfo.setState(true);
        esManager.update(workInfo);
    }


}
