package com.woting.crawler.core.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woting.crawler.core.scheme.model.Scheme;
import com.woting.crawler.exceptionC.Wtcc1001CException;

/**
 * 方案监控线程
 * @author wanghui
 */
public class SchemeMoniter extends Thread {
    private Logger logger = LoggerFactory.getLogger(SchemeMoniter.class);
    private Scheme scheme;

    public SchemeMoniter(Scheme scheme) {
        if (scheme.getCrawlType()>0&&(scheme.getCrawlType()-scheme.getProcessNum()<=0)) {
            throw new Wtcc1001CException("不合法的抓取方案");
        }
        this.scheme=scheme;
        this.setName("抓取方案监控:"+this.scheme.getSchemeName());
    }

    public void run() {
        logger.info("启动抓取方案[{}]监控线程", this.scheme.getSchemeName());
        logger.info("抓取类:{}",scheme.getClassName());
        String crawlType="循环抓取，已抓取<"+scheme.getProcessNum()+">次";
        if (scheme.getCrawlType()>0) crawlType="重复抓取<"+scheme.getCrawlType()+">次，已抓取<"+scheme.getProcessNum()+">次";
        logger.info("抓取类型:{}", crawlType);
        
        logger.info("是否存储页面:{}", scheme.getIsStoreWeb()==1?"存储":"不存储");
        while (true) {
            try {
                Thread.sleep(1000);
                //System.out.println("ABC");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}