package com.woting.crawler.core.scheme.control;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.DateUtils;
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
 * 方案监控线程
 * @author wanghui
 */
public class SchemeMoniter extends Thread {
    private Logger logger = LoggerFactory.getLogger(SchemeMoniter.class);

    private Scheme scheme;//抓取方案
    private SchemeService sService; //方案服务类
    private int curNum;//当前的运行批次
    private CrawlController controller;//crawl4j抓取控制

    public SchemeMoniter(Scheme scheme) {
        this.scheme=scheme;
        this.curNum=this.scheme.getProcessNum()+1;
        this.setName("抓取方案监控:"+this.scheme.getSchemeName()+"-"+this.curNum);
        sService=(SchemeService)SpringShell.getBean("schemeService");
    }

    public void run() {
        logger.info("启动抓取方案[{}]监控线程", scheme.getSchemeName());
        logger.info("抓取类：{}",scheme.getClassName());
        String crawlType="循环执行，已执行<"+scheme.getProcessNum()+">次";
        if (scheme.getCrawlType()>0) crawlType="重复执行<"+scheme.getCrawlType()+">次，已执行<"+scheme.getProcessNum()+">次";
        logger.info("抓取类型：{}", crawlType);
        logger.info("是否存储页面：{}", scheme.getIsStoreWeb()==1?"存储":"不存储");

        //计算Berkeley DB的临时目录
        String dbPath="";
        if (StringUtils.isNullOrEmptyOrSpace(scheme.getTempPath())) {
            dbPath=SystemCache.getCache(CrawlerConstants.APP_PATH).getContent()+"dbStore/"+scheme.getId()+"_"+curNum;
        } else {
            dbPath=scheme.getTempPath();
            if (!dbPath.startsWith("/")) dbPath=SystemCache.getCache(CrawlerConstants.APP_PATH).getContent()+"dbStore/"+scheme.getTempPath();
        }
        logger.debug("{}Berkeley DB临时存储目录[{}]",StringUtils.isNullOrEmptyOrSpace(scheme.getTempPath())?"系统计算":"方案设置",dbPath);
        //计算网页存储
        String storeUrl="";
        if (scheme.getIsStoreWeb()==0) storeUrl="";//不存储
        else {//存储
            String needPath="webStore/"+scheme.getId()+"_"+curNum;
            storeUrl=scheme.getTempStorePath();
            if (StringUtils.isNullOrEmptyOrSpace(storeUrl)) {
                storeUrl=SystemCache.getCache(CrawlerConstants.APP_PATH).getContent()+needPath;
            } else {
                storeUrl+="webStore/"+scheme.getId()+"_"+curNum;
                if (!storeUrl.startsWith("/")) storeUrl=SystemCache.getCache(CrawlerConstants.APP_PATH).getContent()+storeUrl;
            }
        }
        if (StringUtils.isNullOrEmptyOrSpace(storeUrl)) {
            logger.debug("不进行网页存储");
        } else {
            logger.debug("{}网页存储目录[{}]",StringUtils.isNullOrEmptyOrSpace(scheme.getTempStorePath())?"系统计算":"方案设置",storeUrl);
        }
        Map<String, Object> customDataMap=new HashMap<String, Object>();
        customDataMap.put("scheme", scheme);
        customDataMap.put("storeUrl", storeUrl);

        boolean canRun=true;
        while(canRun) {
            try {
                //创建批次信息，并根据逻辑清洗管理数据
                CrawlBatch cBatch=sService.getBatch(scheme.getId(), curNum);
                while (cBatch!=null&&cBatch.getFlag()==1) {
                    cBatch=sService.getBatch(scheme.getId(), ++curNum);
                }
                if (curNum>(scheme.getProcessNum()+1)) {//更新方案信息
                    scheme.setProcessNum(curNum-1);
                    sService.updateScheme(scheme);
                }
                if (scheme.getCrawlType()>0&&curNum>scheme.getCrawlType()) {
                    canRun=false;
                    curNum=scheme.getCrawlType();
                    logger.info("[{}]方案已执行完毕，无须执行",scheme.getSchemeName());
                } else {
                    if (cBatch==null) {
                        cBatch=new CrawlBatch();
                        cBatch.setScheme(scheme);
                        cBatch.setSchemeNum(curNum);
                        cBatch.setBeginTime(new Timestamp(System.currentTimeMillis()));
                        cBatch.setEndTime(new Timestamp(System.currentTimeMillis()));
                        sService.insertBatch(cBatch);
                    } else {//获取上次抓取内容
                        cBatch.setScheme(scheme);
                        sService.initVisitedUrl(cBatch);
                    }
                    scheme.setCrawlBatch(cBatch);

                    long startTime=System.currentTimeMillis();
                    logger.info("开始运行:时间<{}>", DateUtils.convert2LocalStr("yyyy-MM-dd HH:mm:ss:SSS", new Date(startTime)));
                    CrawlConfig cnf = new CrawlConfig();
                    cnf.setCrawlStorageFolder(dbPath);
                    String[] seeds=scheme.getFetchSeeds().split(" ");
                    WebCrawler wc=(WebCrawler)(Class.forName(scheme.getClassName()).newInstance());
                    PageFetcher pageFetcher = new PageFetcher(cnf);
                    controller = new CrawlController(cnf, pageFetcher, new RobotstxtServer(new RobotstxtConfig(), pageFetcher));
                    controller.setCustomData(customDataMap);
                    for (String url: seeds) controller.addSeed(url);
                    new Thread() {
                        public void run() {
                            controller.start(wc.getClass(), scheme.getThreadNum());
                        }
                    }.start();

                    //改写抓取情况，半分钟更新1次
                    int i=0;
                    while (!controller.isFinished()) {
                        sleep(1000);
                        if (i++>30) {
                            i=0;
                            //写一次
                            sService.updateBatchProgress4Fetch(scheme);
                        }
                    }
                    controller.waitUntilFinish();
                    //数据库处理
                    scheme.increaseNum();
                    sService.updateScheme(scheme); //1方案修改
                    sService.finishBatch(scheme.getCrawlBatch());//2批次处理
                    sService.updateBatchProgress4Fetch(scheme);//3更新批处理
                    long endTime=System.currentTimeMillis();
                    logger.info("结束运行：时间 <{}>", DateUtils.convert2LocalStr("yyyy-MM-dd HH:mm:ss:SSS", new Date(endTime)));
                    logger.info("从<{}>开始，到<{}>结束；所用时间：毫秒<{}>。", DateUtils.convert2LocalStr("yyyy-MM-dd HH:mm:ss:SSS", new Date(startTime))
                            ,DateUtils.convert2LocalStr("yyyy-MM-dd HH:mm:ss:SSS", new Date(endTime)), endTime-startTime);
                    if (scheme.getCrawlType()>0&&scheme.getCrawlType()-scheme.getProcessNum()<=0) canRun=false;
                    if (canRun) { //间隔时间处理
                        sleep(scheme.getIntervalTime()<0?0:scheme.getIntervalTime());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                canRun=false;
            }
        }
    }
}