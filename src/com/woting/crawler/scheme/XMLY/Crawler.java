package com.woting.crawler.scheme.XMLY;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import com.spiritdata.framework.util.SpiritRandom;
import com.woting.crawler.ext.SpringShell;
import com.woting.crawler.service.TestService;

public class Crawler extends WebCrawler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 网页是否需要被访问
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        //是否是所要的喜马拉雅的页面
        return href.startsWith("http://www.ximalaya.com")&&(ParseUtils.getType(href)>0);
    }

    @Override
    public void visit(Page page) {
        try {
            Thread.sleep(SpiritRandom.getRandom(new Random(), 100, 10000));
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        String href = page.getWebURL().getURL().toLowerCase();
        logger.info("分析网页：{}", href);
        
        //保存文件先看看
        String fileName="/opt/fetchStore/xmly/pagestore"+href.substring("http://www.ximalaya.com".length());
        if (fileName.endsWith("/")) fileName=fileName.substring(0, fileName.length()-1);
        fileName+=".html";
        try {
            FileUtils.writeByteArrayToFile(new File(fileName), page.getContentData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
//        Map<String, Object> d=new HashMap<String, Object>();
//        d.put("ParentURL", ParseUtils.getType(href)+"");
//        d.put("Type", page.getWebURL().getParentUrl());
//        d.put("URL", href);
//        d.put("Time", new Timestamp(System.currentTimeMillis()));
//        ((TestService)SpringShell.getBean("testService")).insertTest(d);
    }

    
}