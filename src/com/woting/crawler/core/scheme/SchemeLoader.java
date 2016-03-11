package com.woting.crawler.core.scheme;

import java.util.List;

import com.woting.crawler.core.scheme.model.Scheme;
import com.woting.crawler.core.scheme.service.SchemeService;
import com.woting.crawler.ext.SpringShell;

/**
 * 方案加载类
 * @author wanghui
 */
public class SchemeLoader {
    private int loadType;
    private String[] schemeFiles;

    /**
     * 方案加载类型
     * @param loadType 加载类型:1-数据库方式;2-文件方式;
     * @param schemeFiles 若是文件方式，这里是
     */
    public SchemeLoader(int loadType, String[] schemeFiles) {
        super();
        this.loadType = loadType;
        this.schemeFiles = schemeFiles;
    }

    /**
     * 加载并运行抓取方案
     */
    public void load_run() {
        List<Scheme> activeSchemes=null;
        if (loadType==1) { //数据库方式
            SchemeService ss = (SchemeService)SpringShell.getBean("schemeService");
            activeSchemes=ss.getActiveSchemesFromDB();
        }
        if (activeSchemes!=null&&!activeSchemes.isEmpty()) {
            //逐个启动抓取方案
        }
    }

    //开始喜马拉雅的爬取
    //Crawling.start("喜马拉雅", "conf/XMLY.properties");
    //开始蜻蜓的爬取

}