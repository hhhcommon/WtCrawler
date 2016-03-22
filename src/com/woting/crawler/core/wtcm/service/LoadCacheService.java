package com.woting.crawler.core.wtcm.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.service.DictService;
import com.woting.crawler.CrawlerConstants;

@Service
public class LoadCacheService {
    private Logger logger = LoggerFactory.getLogger(LoadCacheService.class);
    @Resource
    private DictService dictService;

    /**
     * 加载内容管理中的资源库数据，为数据导入做准备
     */
    public void loadContent() {
        //加载字典内容
        try {
            _CacheDictionary _cd = dictService.loadCache();
            SystemCache.setCache(new CacheEle<_CacheDictionary>(CrawlerConstants.CM_DICT, "系统字典", _cd));
            logger.info("加载资源库[字典]成功");
        } catch(Exception e) {
            logger.info("加载资源库[字典]失败，{}", e.getMessage());
        }
    }
}