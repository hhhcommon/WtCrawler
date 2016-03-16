package com.woting.crawler.core.etl1;

/**
 * 原始数据内存监控
 * @author wanghui
 */
public class OrigMomoryMoniter extends Thread {
    OrigMemeryData origData;
    long intervalTime;

    public OrigMomoryMoniter(OrigMemeryData origData, long intervalTime) {
        super();
        this.origData = origData;
        this.intervalTime=intervalTime;
    }

    //开始执行监控任务
    public void run() {
        while (true) {
            try {
                
                this.wait(this.intervalTime);//等若干时间
            } catch (InterruptedException e) {
            }
        }
    }
}