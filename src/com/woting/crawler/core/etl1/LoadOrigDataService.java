package com.woting.crawler.core.etl1;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 装载原始数据列表
 * @author wanghui
 */
public abstract class LoadOrigDataService {
    public OrigMemeryData origData;
    public EtlOrigConfig config;

    public void start(OrigMemeryData origData, EtlOrigConfig config) {
        this.origData=origData;
        this.config=config;
        //检查是否需要加载数据
    }
}