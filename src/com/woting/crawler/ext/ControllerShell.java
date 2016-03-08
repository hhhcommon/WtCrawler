package com.woting.crawler.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class ControllerShell<C extends WebCrawler> {
    protected Logger logger;

    private CrawlConfig config;
    private C crawler;
    private String[] urlSeeds;
    private int threadNum;

    protected CrawlController controller;

    public ControllerShell(CrawlConfig conf, C crawler, String[] urlSeeds, int threadNum) {
        this.config=conf;
        this.crawler=crawler;
        this.urlSeeds=urlSeeds;
        this.threadNum=threadNum;
    }

    public void start(String taskName) throws Exception {
        logger = LoggerFactory.getLogger(this.getClass());

        long startTime=System.currentTimeMillis();
        logger.info("["+taskName+"]开始时间 {}", startTime);

        PageFetcher pageFetcher = new PageFetcher(config);
        controller = new CrawlController(config, pageFetcher, new RobotstxtServer(new RobotstxtConfig(), pageFetcher));
        for (String url: urlSeeds) controller.addSeed(url);
        controller.start(this.crawler.getClass(), this.threadNum);
//        controller.start(com.woting.crawler.scheme.XMLY.Crawler.class, this.threadNum);
        while (!controller.isFinished()){};//这是个死循环，等待

        long endTime=System.currentTimeMillis();
        logger.info("["+taskName+"]结束时间 {}", endTime);
        logger.info("["+taskName+"]开始时间 {}, 结束时间 {}, 所用时间毫秒 {}", startTime, endTime, endTime-startTime);
    }
}