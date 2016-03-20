package com.woting.crawler.scheme.XMLY.etl1;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woting.crawler.core.etl1.Etl1Process;
import com.woting.crawler.ext.SpringShell;
import com.woting.crawler.scheme.XMLY.XmlyService;

public class EtlProcess implements Etl1Process{
    private Logger logger = LoggerFactory.getLogger(EtlProcess.class);

    public Object batchLock;//修改batch内容的数据；这个还需要多想一想
    private XmlyService xmlyService; //喜马拉雅服务类

    @Override
    public void init() {
        xmlyService=(XmlyService)SpringShell.getBean("xmlyService");
        batchLock=new Object();
        //加载树
        
    }

    @Override
    public List<Map<String, Object>> getOrigDataList(int pageSize, int pageNum) {
        return xmlyService.getOrigDataList(pageSize, pageNum);
    }

    @Override
    public void dealOneData(Map<String, Object> oneData) {
        //分析喜马拉雅数据，直接加入正式库
        logger.info("{}处理数据::{}", "线程内", oneData.get("assetType")+":"+oneData.get("assetName")+":"+oneData.get("seqName"));
        
    }
}