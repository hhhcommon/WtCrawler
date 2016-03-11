package com.woting.crawler.core.control;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woting.crawler.core.scheme.model.Scheme;
import com.woting.crawler.core.scheme.service.SchemeService;
import com.woting.crawler.ext.SpringShell;

/**
 * 主控制程序
 * @author wanghui
 */
public class MainController {
    private Logger logger = LoggerFactory.getLogger(MainController.class);
    private List<Scheme> activeSchemes;//活动的可处理的方案

    /**
     * 方案加载类型
     * @param loadType 加载类型:1-数据库方式;2-文件方式;
     * @param schemeFiles 若是文件方式，这里是
     */
    public void loadScheme(int loadType, String[] schemeFiles) {
        this.activeSchemes=new ArrayList<Scheme>();
        logger.info("加载模式<{}>", activeSchemes.size());
        if (loadType==1) { //数据库方式
            SchemeService ss = (SchemeService)SpringShell.getBean("schemeService");
            activeSchemes=ss.getActiveSchemesFromDB();
        } else { //文件方式
        }
    }

    public void runningScheme() {
        if (activeSchemes!=null&&!activeSchemes.isEmpty()) {
            for (Scheme s: activeSchemes) {
                SchemeMoniter sm=new SchemeMoniter(s);
                sm.start();
            }
        }
    }
}