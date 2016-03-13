package com.woting.crawler.core.control;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.CrawlerConstants;
import com.woting.crawler.core.scheme.model.CrawlBatch;
import com.woting.crawler.core.scheme.model.Scheme;
import com.woting.crawler.core.scheme.service.SchemeService;
import com.woting.crawler.ext.SpringShell;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * 一次抓取
 * @author wanghui
 */
public class Crawling extends Thread {
    private Logger logger = LoggerFactory.getLogger(Crawling.class);

    private Scheme scheme;
    private int curNum;//当前的运行批次
    private CrawlController controller;//crawl4j抓取控制

    private SchemeService sService; //方案服务类

    public Crawling(Scheme scheme) {
        this.scheme=scheme;
        this.curNum=this.scheme.getProcessNum()+1;
        this.setName("["+this.scheme.getSchemeName()+":"+this.curNum+"]方案");
        sService=(SchemeService)SpringShell.getBean("schemeService");
    }

    public CrawlController getContraller() {
        return this.controller;
    }

    public void run() {
        try {
            //计算Berkeley DB的临时目录
            String dbPath="";
            if (StringUtils.isNullOrEmptyOrSpace(scheme.getTempPath())) {
                dbPath=SystemCache.getCache(CrawlerConstants.APP_PATH).getContent()+"/dbStore/"+scheme.getId()+"_"+this.curNum;
            } else {
                dbPath=scheme.getTempPath();
                if (!dbPath.startsWith("/")) dbPath=SystemCache.getCache(CrawlerConstants.APP_PATH).getContent()+"/dbStore/"+scheme.getTempPath();
            }
            logger.debug("{}Berkeley DB临时存储目录[{}]",StringUtils.isNullOrEmptyOrSpace(scheme.getTempPath())?"系统计算":"方案设置",dbPath);
            //计算网页存储
            String storeUrl="";
            if (scheme.getIsStoreWeb()==0) storeUrl="";//不存储
            else {//存储
                storeUrl=scheme.getTempStorePath();
                if (StringUtils.isNullOrEmptyOrSpace(storeUrl)) {
                    storeUrl=SystemCache.getCache(CrawlerConstants.APP_PATH).getContent()+"/webStore/"+scheme.getId()+"_"+this.curNum;
                } else {
                    if (!storeUrl.startsWith("/")) storeUrl=SystemCache.getCache(CrawlerConstants.APP_PATH).getContent()+"/webStore/"+storeUrl;
                }
            }
            if (StringUtils.isNullOrEmptyOrSpace(storeUrl)) {
                logger.debug("不进行网页存储");
            } else {
                logger.debug("{}网页存储目录[{}]",StringUtils.isNullOrEmptyOrSpace(scheme.getTempStorePath())?"系统计算":"方案设置",storeUrl);
            }
            Map<String, Object> customDataMap=new HashMap<String, Object>();
            customDataMap.put("scheme", this.scheme);
            customDataMap.put("storeUrl", storeUrl);

            //创建批次信息
            CrawlBatch cBatch=sService.getBatch(scheme.getId(), this.curNum);
            if (cBatch==null) {
                cBatch=new CrawlBatch();
                cBatch.setScheme(scheme);
                cBatch.setSchemeNum(this.curNum);
                cBatch.setBeginTime(new Timestamp(System.currentTimeMillis()));
                sService.insertBatch(cBatch);
            } else {//获取上次抓取内容
                sService.initVisitedUrl(scheme.getId(), this.curNum, cBatch);
            }
            scheme.setCrawlBatch(cBatch);

            CrawlConfig cnf = new CrawlConfig();
            cnf.setCrawlStorageFolder(dbPath);
            String[] seeds=scheme.getFetchSeeds().split(" ");
            WebCrawler wc=(WebCrawler)(Class.forName(scheme.getClassName()).newInstance());
            PageFetcher pageFetcher = new PageFetcher(cnf);
            this.controller = new CrawlController(cnf, pageFetcher, new RobotstxtServer(new RobotstxtConfig(), pageFetcher));
            this.controller.setCustomData(customDataMap);
            for (String url: seeds) this.controller.addSeed(url);
            this.controller.start(wc.getClass(), scheme.getThreadNum());
            this.controller.setPageFetcher(pageFetcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}