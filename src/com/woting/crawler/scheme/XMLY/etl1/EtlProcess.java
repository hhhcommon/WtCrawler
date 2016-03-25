package com.woting.crawler.scheme.XMLY.etl1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woting.cm.core.common.model.Owner;
import com.woting.crawler.core.etl1.Etl1Process;
import com.woting.crawler.core.wtcm.service.StoreAssetService;
import com.woting.crawler.core.wtcm.service.StoreDictService;
import com.woting.crawler.ext.SpringShell;
import com.woting.crawler.scheme.XMLY.XmlyService;

public class EtlProcess implements Etl1Process{
    private Logger logger = LoggerFactory.getLogger(EtlProcess.class);

    public Object batchLock;//修改batch内容的数据；这个还需要多想一想
    private XmlyService xmlyService; //喜马拉雅服务类
    private StoreDictService storeDictService; //喜马拉雅-存储字典服务类
    private StoreAssetService storeAssetService; //喜马拉雅-存储资产服务类
    private Owner o;

    @Override
    public void init() {
        xmlyService=(XmlyService)SpringShell.getBean("xmlyService");
        storeDictService=(StoreDictService)SpringShell.getBean("storeDictService");
        storeAssetService=(StoreAssetService)SpringShell.getBean("storeAssetService");
        o=new Owner(101,"2");//定义为喜马拉雅
    }

    @Override
    public List<Map<String, Object>> getOrigDataList(int pageSize, int pageNum) {
        return xmlyService.getOrigDataList(pageSize, pageNum);
    }

    @Override
    public void dealOneData(Map<String, Object> oneData) {
        //分析喜马拉雅数据，直接加入正式库
        logger.info("处理数据::{}", oneData.get("assetType")+":【"+oneData.get("assetId")+"-"+oneData.get("assetName")+"】:【"+oneData.get("seqId")+""+oneData.get("seqName")+"】");
        //得到资产分类
        int assetType=0;
        try {
            assetType=Integer.parseInt(oneData.get("assetType")+"");
        } catch(Exception e) {};
        if (assetType==0) {
            logger.info("无法获得合法的");
            return;
        }

        Map<String, Object> tempMap=null;
        //一、处理字典
        List<String> dictRefs=new ArrayList<String>();
        String retSaveDict;
        //导入分类
        String cataName=(oneData.get("catalog")+"").toUpperCase().trim();
        if (!cataName.equals("null")&&!cataName.equals("")) {
            cataName=cataName.replace("【", "");
            cataName=cataName.replace("】", "");
            tempMap=new HashMap<String, Object>();
            tempMap.put("nodeName", cataName);
            retSaveDict=storeDictService.saveDict("3", "-1", o, tempMap);
            if (retSaveDict.indexOf("::")!=-1) {
                dictRefs.add(retSaveDict.substring(retSaveDict.indexOf("::")+2));
            }
        }
        //导入关键词
        if (oneData.get("tags")!=null) {
            String tags=(oneData.get("tags")+"").toUpperCase().trim();
            String[] _tags=tags.split(",");
            for (String tag: _tags) {
                tempMap=new HashMap<String, Object>();
                tempMap.put("nodeName", tag);
                storeDictService.saveDict("6", "-1", o, tempMap);
            }
        }
        //二、处理媒体资产
        if (assetType==1) {//专辑
            storeAssetService.saveAsset(oneData);
        } else if (assetType==2) {//声音
            storeAssetService.saveSequ(oneData);
        }
        //三、资产发布:喜马拉雅自动就发布了
        
        //四、设置这条记录已经被处理过了
        tempMap=new HashMap<String, Object>();
        tempMap.put("id", oneData.get("id"));
        tempMap.put("flag", 1);//已经处理
        xmlyService.updateOrig(tempMap);
    }
}