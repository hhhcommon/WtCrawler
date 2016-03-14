package com.woting.crawler.scheme.XMLY;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.SpiritRandom;
import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.core.scheme.model.Scheme;
import com.woting.crawler.ext.SpringShell;

public class Crawler extends WebCrawler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Scheme s=null;
    private String tempStorePath="";
    private boolean needStoreWeb=false;

    private XmlyService xmlyService; //方案服务类

    /**
     * 启动时执行
     */
    @Override
    public void onStart() {
        s=(Scheme)((Map<String, Object>)(this.getMyController().getCustomData())).get("scheme");
        tempStorePath=(String)((Map<String, Object>)(this.getMyController().getCustomData())).get("storeUrl");
        needStoreWeb=!StringUtils.isNullOrEmptyOrSpace(tempStorePath);
        xmlyService=(XmlyService)SpringShell.getBean("xmlyService");
    }

    /**
     * 网页是否需要被访问
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        href=href.trim().toLowerCase();
        //url判断
        if (!href.startsWith("http://www.ximalaya.com")) return false;
        if (ParseUtils.getType(href)<=0) return false;
        //是否已经访问过了
        if (s.getCrawlBatch().isVisited(href)) return false;
        return true;
    }

    @Override
    public void visit(Page page) {
        try {
            Thread.sleep(SpiritRandom.getRandom(new Random(), 100, 10000));
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        String href = page.getWebURL().getURL().toLowerCase();
        href=href.trim().toLowerCase();

        if (ParseUtils.getType(href)>0) {
            logger.info("分析网页：{}", href);
            //保存文件
            if (needStoreWeb) {
                String fileName=tempStorePath+href.substring("http://www.ximalaya.com".length());
                if (fileName.endsWith("/")) fileName=fileName.substring(0, fileName.length()-1);
                fileName+=".html";
                try {
                    FileUtils.writeByteArrayToFile(new File(fileName), page.getContentData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //分析内容
            Map<String, Object> parseData=new HashMap<String, Object>();
            parseData.put("id", SequenceUUID.getUUIDSubSegment(4));
            parseData.put("schemeId", s.getId());
            parseData.put("schemeNum", s.getProcessNum()+1);
            parseData.put("visitUrl", href);
            parseData.put("parentUrl", StringUtils.isNullOrEmptyOrSpace(page.getWebURL().getParentUrl())?"":page.getWebURL().getParentUrl());
            parseData.put("assetType", ParseUtils.getType(href));

            /**
            parseData.put("seqId", "");
            parseData.put("seqName", "");
            parseData.put("assetId", "");
            parseData.put("assetName", "");
            parseData.put("playUrl", "");
            parseData.put("person", "");
            parseData.put("imgUrl", "");
            parseData.put("playCount", "");
            parseData.put("catalog", "");
            parseData.put("tags", "");
            parseData.put("descript", "");
            parseData.put("extInfo", "");
             */
            xmlyService.insertOrig(parseData);
            s.getCrawlBatch().addVisitedUrl(href);
        }
    }
}