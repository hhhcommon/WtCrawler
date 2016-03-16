package com.woting.crawler.core.scheme.control;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spiritdata.framework.util.DateUtils;
import com.woting.crawler.core.scheme.model.Scheme;
import com.woting.crawler.core.scheme.service.SchemeService;
import com.woting.crawler.ext.SpringShell;

/**
 * 方案监控线程
 * @author wanghui
 */
public class SchemeMoniter extends Thread {
    private Logger logger = LoggerFactory.getLogger(SchemeMoniter.class);

    private Scheme scheme;//抓取方案
    private SchemeService sService; //方案服务类

    public SchemeMoniter(Scheme scheme) {
        this.scheme=scheme;
        this.setName("抓取方案监控:"+this.scheme.getSchemeName());
        sService=(SchemeService)SpringShell.getBean("schemeService");
    }

    public void run() {
        logger.info("启动抓取方案[{}]监控线程", this.scheme.getSchemeName());
        logger.info("抓取类：{}",scheme.getClassName());
        String crawlType="循环执行，已执行<"+scheme.getProcessNum()+">次";
        if (scheme.getCrawlType()>0) crawlType="重复执行<"+scheme.getCrawlType()+">次，已执行<"+scheme.getProcessNum()+">次";
        logger.info("抓取类型：{}", crawlType);
        logger.info("是否存储页面：{}", scheme.getIsStoreWeb()==1?"存储":"不存储");

        Crawling cling=null;
        boolean canRun=true;
        boolean etlRun=true;
        while(canRun) {
            try {
                String schemeDesc=scheme.getSchemeName()+"::"+(scheme.getProcessNum()+1);
                long startTime=System.currentTimeMillis();
                logger.info("[{}]开始运行:时间<{}>", schemeDesc, DateUtils.convert2LocalStr("yyyy-MM-dd HH:mm:ss:SSS", new Date(startTime)));
                cling=new Crawling(this.scheme);
                cling.start();

                while (cling.getContraller()==null) sleep(10);
                //改写抓取情况，半分钟更新1次
                int i=0;
                while (!cling.getContraller().isFinished()) {
                    sleep(1000);
                    if (i++>30) {
                        i=0;
                        //写一次
                        sService.updateBatchProgress4Fetch(scheme);
                    }
                }
                cling.getContraller().waitUntilFinish();
                //数据库处理
                sService.updateScheme(scheme); //1方案修改
                sService.finishBatch(scheme.getCrawlBatch());//2批次处理
                sService.updateBatchProgress4Fetch(scheme);//3更新批处理
                long endTime=System.currentTimeMillis();
                logger.info("[{}]结束运行：时间 <{}>", schemeDesc, DateUtils.convert2LocalStr("yyyy-MM-dd HH:mm:ss:SSS", new Date(startTime)));
                logger.info("[{}]所用时间：毫秒<{}>", schemeDesc, endTime-startTime);

                this.scheme.increaseNum();
                if (this.scheme.getCrawlType()>0&&this.scheme.getCrawlType()-this.scheme.getProcessNum()<=0) canRun=false;
                if (canRun) { //间隔时间处理
                    sleep(scheme.getIntervalTime()<0?0:scheme.getIntervalTime());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                canRun=false;
            }
        }
    }
}