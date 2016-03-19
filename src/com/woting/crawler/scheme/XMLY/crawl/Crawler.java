package com.woting.crawler.scheme.XMLY.crawl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

import com.spiritdata.framework.util.SpiritRandom;
import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.core.scheme.model.Scheme;
import com.woting.crawler.ext.SpringShell;
import com.woting.crawler.scheme.XMLY.XmlyService;

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
        return true;
    }

    @Override
    public void visit(Page page) {
        try {
            Thread.sleep(SpiritRandom.getRandom(new Random(), 10, 100));
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        String href = page.getWebURL().getURL().toLowerCase();
        href=href.trim().toLowerCase();
        //是否已经处理过了
        if (s.getCrawlBatch().isVisited(href)) return;

        int pageType=ParseUtils.getType(href);
        if (pageType>0) {
            s.getCrawlBatch().visitCount_IncrementAndGet();
            logger.info("分析网页：{}", href);
            //保存文件
            if (needStoreWeb) {
                if (href.indexOf("?")!=-1) {
                    href=href.substring(0, href.indexOf("?"))+"("+href.substring(href.indexOf("?")+1)+")";
                }
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
            parseData.put("id", DigestUtils.md5Hex(this.s.getId()+"::"+(s.getProcessNum()+1)+"::"+href));
            parseData.put("schemeId", s.getId());
            parseData.put("schemeNum", s.getProcessNum()+1);
            parseData.put("visitUrl", href);
            parseData.put("parentUrl", StringUtils.isNullOrEmptyOrSpace(page.getWebURL().getParentUrl())?"":page.getWebURL().getParentUrl());
            parseData.put("assetType", ParseUtils.getType(href));

            switch (pageType) {
            case 1:
                ParseUtils.parseAlbum(page.getContentData(), parseData);
                break;
            case 2:
                ParseUtils.parseSond(page.getContentData(), parseData);
                break;
/*            case 3:
                ParseUtils.parseZhubo(page.getContentData(), parseData);
                break;
            case 4:
                ParseUtils.parseTags(page.getContentData(), parseData);
                break;*/
            }
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
            //类型处理
            int flag=0;
            if ((pageType==1)&&(parseData.get("seqId")==null||(parseData.get("seqId")+"").trim().length()==0)) flag=2;
            else
            if ((pageType==2)&&(parseData.get("playUrl")==null||(parseData.get("playUrl")+"").trim().length()==0)) flag=2;
            parseData.put("flag", flag);
            synchronized(s.getCrawlBatch().lock) {
                if (!s.getCrawlBatch().isVisited(href)) {
                    s.getCrawlBatch().insertCount_IncrementAndGet();
                    xmlyService.insertOrig(parseData);
                }
            }
        }
    }
}