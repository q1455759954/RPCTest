package server.es;



/**
 * ElasticSearch操作类接口
 * @author zchuanzhao
 * 2018/7/2
 */
public interface IEsManager {
    /**
     * 保存对象
     * @param obj
     * @return
     */
    boolean save(Object obj);

    /**
     * 更新
     * @param obj
     * @return
     */
    boolean update(Object obj);

    /**
     * 删除
     * @param unid
     * @param beanClass
     * @return
     */
    boolean delete(String unid, Class beanClass);

    /**
     * 获取单个对象
     * @param unid
     * @param beanClass
     * @param <T>
     * @return
     */
    <T> T get(String unid, Class<T> beanClass);

    /**
     * 获取分页列表
     * @param queryInfo
     * @param beanClass
     * @param defaultSort
     * @param <T>
     * @return
     */
    <T > GridData<T> getList(QueryInfo queryInfo, Class<T> beanClass, String defaultSort);
}
