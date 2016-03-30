package com.woting.crawler.scheme.XMLY.etl1;

import java.util.Map;

import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;

public abstract class ConvertUtils {
    public static MediaAsset convert2Ma(Map<String, Object> oneData) {
        MediaAsset ma=new MediaAsset();
        if (oneData.get("assetId")!=null) return null;
        ma.setId(oneData.get("assetId")+""); //注意这里是原始Id
        if (oneData.get("tags")!=null) ma.setKeyWords(oneData.get("tags")+"");
        
        return ma;
    }
    public static SeqMediaAsset convert2Sma(Map<String, Object> oneData) {
        SeqMediaAsset sma=new SeqMediaAsset();
        
        sma.setKeyWords(oneData.get("tags")+"");
        if (oneData.get("tags")!=null) sma.setKeyWords(oneData.get("tags")+"");
        return sma;
    }
}