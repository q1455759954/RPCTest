package server.api;

import server.entity.SearchInfo;
import server.es.QueryInfo;

public interface UserService {

    /**
     * 得到推荐
     * @param uId
     * @param since_id
     * @return
     */
    String[] getRecommends(String uId, String since_id);

    void LogToHive(String json);

    SearchInfo getUserInfo(String queryInfo);

    SearchInfo getStatusInfo(String queryInfo);

    void addUser(String userInfo);

    void updateUser(String userInfo);

    void addStatusContent(String statusContent);

    void addAppointment(String appointment);

    String classify(String text);

    void addCommodity(String info);

    void addWorkInfo(String info);

    SearchInfo getAppointmentInfo(String queryInfo);

    SearchInfo getCommodityInfo(String queryInfo);

    SearchInfo getWorkInfo(String toString);
    void updateAppointment(String row_key);

    void updateCommodity(String row_key);

    void updateWorkInfo(String row_key);

}
