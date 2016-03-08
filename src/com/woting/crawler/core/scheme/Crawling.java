package com.woting.crawler.core.scheme;

import java.io.FileInputStream;
import java.util.Properties;

import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.CrawlerConstants;
import com.woting.crawler.ext.ControllerShell;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.WebCrawler;

/**
 * 爬取得基础类
 * @author wanghui
 */
public class Crawling {
    /**
     * 静态方法，开始一个爬取任务
     * @param name 任务名称
     * @param confFileName 任务的配置文件名称
     * @throws Exception
     */
    public static void start(String name, String confFileName) throws Exception {
        if (StringUtils.isNullOrEmptyOrSpace(name)) {
            throw new IllegalArgumentException("爬取工程名称不能为空");
        }
        if (StringUtils.isNullOrEmptyOrSpace(confFileName)) {
            throw new IllegalArgumentException("配置文件名称不能为空");
        }
        //配置文件读取
        Properties conf = new Properties();
        String _fn=confFileName;
        if (!confFileName.startsWith("/")) {
            _fn=SystemCache.getCache(CrawlerConstants.APP_PATH).getContent()+"/"+_fn;
        }
        conf.load(new FileInputStream(_fn));

        //设置config
        CrawlConfig xmlyConf = new CrawlConfig();
        xmlyConf.setCrawlStorageFolder(conf.getProperty("BerkeleyDB"));//这个文件位置是为Berkeley DB使用的
        //创建Controller
        String[] seeds=(conf.getProperty("FetchSeeds")).split(" ");
        WebCrawler wc = (WebCrawler)(Class.forName(conf.getProperty("CrawlClass")).newInstance());
        ControllerShell<WebCrawler> xmlyController= new ControllerShell<WebCrawler>(xmlyConf, wc, seeds, Integer.parseInt(conf.getProperty("threadNum")));
        xmlyController.start(name);
        //启动Controller
    }
}