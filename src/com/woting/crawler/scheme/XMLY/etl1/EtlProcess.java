package com.woting.crawler.scheme.XMLY.etl1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woting.cm.core.common.model.Owner;
import com.woting.crawler.core.etl1.Etl1Process;
import com.woting.crawler.core.wtcm.service.StoreDictService;
import com.woting.crawler.ext.SpringShell;
import com.woting.crawler.scheme.XMLY.XmlyService;

public class EtlProcess implements Etl1Process{
    private Logger logger = LoggerFactory.getLogger(EtlProcess.class);

    public Object batchLock;//修改batch内容的数据；这个还需要多想一想
    private XmlyService xmlyService; //喜马拉雅服务类
    private StoreDictService storeDictService; //喜马拉雅服务类
    private Owner o;

    @Override
    public void init() {
        xmlyService=(XmlyService)SpringShell.getBean("xmlyService");
        storeDictService=(StoreDictService)SpringShell.getBean("storeDictService");
        o=new Owner(101,"2");//定义为喜马拉雅
    }

    @Override
    public List<Map<String, Object>> getOrigDataList(int pageSize, int pageNum) {
        return xmlyService.getOrigDataList(pageSize, pageNum);
    }

    @Override
    public void dealOneData(Map<String, Object> oneData) {
        //分析喜马拉雅数据，直接加入正式库
        logger.info("{}处理数据::{}", "线程内", oneData.get("assetType")+":"+oneData.get("assetName")+":"+oneData.get("seqName"));
        Map<String, Object> tempMap=null;
        //导入分类
        String cataName=(oneData.get("catalog")+"").toUpperCase().trim();
        if (!cataName.equals("null")&&!cataName.equals("")) {
            cataName=cataName.replace("【", "");
            cataName=cataName.replace("】", "");
            tempMap=new HashMap<String, Object>();
            tempMap.put("nodeName", cataName);
            storeDictService.saveDict("3", "-1", o, tempMap);
        }
        //导入关键词
        String tags=(oneData.get("catalog")+"").toUpperCase().trim();
        String[] _tags=tags.split(",");
        for (String tag: _tags) {
            tempMap=new HashMap<String, Object>();
            tempMap.put("nodeName", tag);
            storeDictService.saveDict("6", "-1", o, tempMap);
        }
        //创建媒体资源
        //建立关键词与媒体之间的关系
        //建立分类与媒体之间的关系
        //设置这条记录已经被处理过了
        tempMap=new HashMap<String, Object>();
        tempMap.put("id", oneData.get("id"));
        tempMap.put("flag", 1);//已经处理
        xmlyService.updateOrig(tempMap);
    }
}