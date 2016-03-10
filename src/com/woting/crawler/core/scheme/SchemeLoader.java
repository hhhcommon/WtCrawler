package com.woting.crawler.core.scheme;

/**
 * 模式加载类
 * @author wanghui
 */
public class SchemeLoader {
    private int loadType;
    private String[] schemeFiles;

    /**
     * 模式加载类型
     * @param loadType 加载类型:1-数据库方式;2-文件方式;
     * @param schemeFiles 若是文件方式，这里是
     */
    public SchemeLoader(int loadType, String[] schemeFiles) {
        super();
        this.loadType = loadType;
        this.schemeFiles = schemeFiles;
    }

    /**
     * 加载并运行抓取模式
     */
    public void load_run() {
        
        if (loadType==1) { //数据库方式
            //读取列表
        }
    }
    
    //开始喜马拉雅的爬取
    //Crawling.start("喜马拉雅", "conf/XMLY.properties");
    //开始蜻蜓的爬取

}