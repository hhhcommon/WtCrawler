package com.woting.crawler.core.control;

import java.util.List;

import com.woting.crawler.core.scheme.model.Scheme;

/**
 * 主控制程序
 * @author wanghui
 */
public class MainController {
    private List<Scheme> activeSchemes;//活动的可处理的方案

    /**
     * 方案加载类型
     * @param loadType 加载类型:1-数据库方式;2-文件方式;
     * @param schemeFiles 若是文件方式，这里是
     */
    public void loadScheme(int loadType, String[] schemeFiles) {
        
    }

    public void runningScheme() {
        if (activeSchemes!=null&&!activeSchemes.isEmpty()) {
            //为每一个可运行的方案启动一个监控线程
        }
    }
}