package com.woting.crawler.core.control;

import com.woting.crawler.core.scheme.model.Scheme;
import com.woting.crawler.exceptionC.Wtcc1001CException;

/**
 * 方案监控线程
 * @author wanghui
 */
public class SchemeMoniter extends Thread {
    private Scheme scheme;

    public SchemeMoniter(Scheme scheme) {
        if (scheme.getCrawlType()>0&&(scheme.getCrawlType()-scheme.getProcessNum()<=0)) {
            throw new Wtcc1001CException("不合法的抓取方案");
        }
        this.scheme=scheme;
        this.setName("抓取方案监控:"+scheme.getSchemeName());
    }

    public void run() {
        
    }
}