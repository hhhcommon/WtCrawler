package com.woting.crawler.core.etl1;

import java.util.List;
import java.util.Map;

/**
 * 第一次Etl过程的处理接口
 * @author wanghui
 */
public interface Etl1Process {
    /**
     * 初始化，在实例创建后马上执行
     */
    public void init();

    /**
     * 从数据库得到原始数据列表，分页得到，这是为处理大数据设计的
     * @param pageSize 每页数据量
     * @param pageNum 页数
     * @return 原始数据列表
     */
    public List<Map<String, Object>> getOrigDataList(int pageSize, int pageNum);

    /**
     * 处理其中的一条数据。这里需注意：
     * <pre>
     * 1-数据处理后要把数据库中的flag字段设置为合适的值：1=处理成功；2=无需处理；3=处理失败
     * 2-同一条数据可能被处理多次，要注意消重复
     * </pre>
     * @param oneData
     */
    public void dealOneData(Map<String, Object> oneData);
}