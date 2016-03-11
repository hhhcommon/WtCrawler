package com.woting.crawler.core.control;

import com.woting.crawler.core.scheme.model.Scheme;

/**
 * 方案监控线程
 * @author wanghui
 */
public class SchemeMoniter extends Thread {
    private Scheme scheme;

    public SchemeMoniter(Scheme scheme) {
        
        this.scheme=scheme;
    }

    public void run() {
        
    }
}