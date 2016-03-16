package com.woting.crawler.core.etl1;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;
import com.spiritdata.framework.util.StringUtils;

/**
 * Etl的运行
 * @author wanghui
 */
public class EtlRunning {
    //原始数据的存放队列
    private OrigMemeryData origData=null;

    public void start(EtlOrigConfig config) {
        if (config==null) throw new IllegalArgumentException("原始数据ETL必须设置");
        if (StringUtils.isNullOrEmptyOrSpace(config.getEtlName())) throw new IllegalArgumentException("ETL名称必须设置");
        origData=new OrigMemeryData();

        try {
            //启动装载线程
            LoadOrigDataService loadOrigService = (LoadOrigDataService)(Class.forName(config.getLoadOrigClassName()).newInstance());
            loadOrigService.start(origData, config);

            //启动清楚线程
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}