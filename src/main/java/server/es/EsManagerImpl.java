package server.es;

import net.sf.json.JSONObject;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.List;


/**
 * ElasticSearch操作类
 * @author zchuanzhao
 * 2018/7/2
 */
public class EsManagerImpl implements IEsManager {

    /**
     * TCP连接客户端
     */
    private TransportClient client;

    public EsManagerImpl() {
        this.client = EsHandler.getClient();
    }

    /**
     * 保存对象
     * @param obj
     * @return
     */
    @Override
    public boolean save(Object obj) {
        EsHandler.mapping(obj);
        String className = obj.getClass().getSimpleName().toLowerCase();
        JSONObject json = JSONObject.fromObject(obj);
        json.put("id",json.get("id"));
        IndexResponse response = client.prepareIndex(EsHandler.ES_CLUSTER_NAME, className, String.valueOf(json.get("id"))).setSource(json).get();
        return response.isCreated();
    }

    /**
     * 更新
     * @param obj
     * @return
     */
    @Override
    public boolean update(Object obj) {
        String className = obj.getClass().getSimpleName().toLowerCase();
        JSONObject json = JSONObject.fromObject(obj);
        UpdateResponse response = client.prepareUpdate(EsHandler.ES_CLUSTER_NAME, className, String.valueOf(json.get("id")))
                .setDoc(json)
                .get();

        return !response.isCreated();
    }


    /**
     * 删除
     * @param unid
     * @param beanClass
     * @return
     */
    @Override
    public boolean delete(String unid, Class beanClass) {
        DeleteResponse response = client.prepareDelete(EsHandler.ES_CLUSTER_NAME, beanClass.getSimpleName().toLowerCase(), unid).get();
        return response.isFound();
    }

    /**
     * 获取单个对象
     * @param unid
     * @param beanClass
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String unid, Class<T> beanClass) {
        GetResponse response = client.prepareGet(EsHandler.ES_CLUSTER_NAME, beanClass.getSimpleName().toLowerCase(), unid).get();
        if (response.isExists()) {
            String jsonStr = response.getSourceAsString();
            JSONObject json = JSONObject.fromObject(jsonStr);
            //TODO json中beanFlag保存的是null字符串，转成bean会报错，所以暂时先设置为null
            json.put("beanFlag",null);
            T bean = (T) JSONObject.toBean(json, beanClass);
            //TODO json -> bean后，time会变成系统当前时间，暂时先用以下方式解决
//            bean.setTime(json.getLong("time"));
            return bean;
        }
        return null;
    }

    /**
     * 获取分页列表
     * @param queryInfo
     * @param beanClass
     * @param defaultSort
     * @param <T>
     * @return
     */
    @Override
    public <T> GridData<T> getList(QueryInfo queryInfo, Class<T> beanClass, String defaultSort) {
        SearchRequestBuilder builder = client.prepareSearch(EsHandler.ES_CLUSTER_NAME).setTypes(beanClass.getSimpleName().toLowerCase()).
                setSearchType(SearchType.DEFAULT);
        //设置查询条件信息
        EsDataGridUtils.parseQueryInfo(builder, queryInfo, beanClass, defaultSort);

        SearchResponse response = builder.execute().actionGet();
        SearchHits hits = response.getHits();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < hits.getHits().length; i++) {
            String jsonStr = hits.getHits()[i].getSourceAsString();
            System.out.println(jsonStr);
            JSONObject json = JSONObject.fromObject(jsonStr);
            //TODO json中beanFlag保存的是null字符串，转成bean会报错，所以暂时先设置为null
            json.put("beanFlag", null);
            T bean = (T) JSONObject.toBean(json, beanClass);
            //TODO json -> bean后，time会变成系统当前时间，暂时先用以下方式解决
//            bean.setTime(json.getLong("time"));
            list.add(bean);
        }

        GridData<T> gridData = new GridData<>();
        gridData.setData(list);
        gridData.setTotal((int) response.getHits().getTotalHits());
        return gridData;
    }
}