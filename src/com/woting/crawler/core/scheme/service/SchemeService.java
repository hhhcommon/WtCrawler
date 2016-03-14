package com.woting.crawler.core.scheme.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.Page;
import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.core.scheme.model.CrawlBatch;
import com.woting.crawler.core.scheme.model.Scheme;
import com.woting.crawler.core.scheme.persis.po.CrawlBatchPo;
import com.woting.crawler.core.scheme.persis.po.SchemePo;
import com.woting.crawler.core.scheme.persis.po.SourcePo;
import com.woting.crawler.exceptionC.Wtcc1001CException;

import edu.uci.ics.crawler4j.crawler.WebCrawler;

@Service
public class SchemeService {
    private Logger logger = LoggerFactory.getLogger(SchemeService.class);

    @Resource(name="defaultDAO")
    private MybatisDAO<SourcePo> sourceDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<SchemePo> schemeDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<CrawlBatchPo> batchDao;

    @PostConstruct
    public void initParam() {
        sourceDao.setNamespace("C_SCHEME");
        schemeDao.setNamespace("C_SCHEME");
        batchDao.setNamespace("C_SCHEME");
    }

    public List<Scheme> getActiveSchemesFromDB() {
        List<SchemePo> asList = schemeDao.queryForList("getActiveSchemes");
        if (asList==null||asList.isEmpty()) return null;
        List<Scheme> retl=new ArrayList<Scheme>();
        for (SchemePo sPo: asList) {
            try {
                Scheme s=new Scheme();
                s.buildFromPo(sPo);
                if (s.getCrawlType()>0&&(s.getCrawlType()-s.getProcessNum()<=0)) {
                    throw new Wtcc1001CException("不合法的抓取方案:本方案需重复执行<"+s.getCrawlType()+">次，已执行<"+s.getProcessNum()+">，无需执行");
                }
                if (StringUtils.isNullOrEmptyOrSpace(s.getSchemeName())) {
                    throw new Wtcc1001CException("不合法的抓取方案：方案名称必须设置");
                }
                if (StringUtils.isNullOrEmptyOrSpace(s.getFetchSeeds())) {
                    throw new Wtcc1001CException("不合法的抓取方案:方案抓取种子Url必须设置");
                }
                if (StringUtils.isNullOrEmptyOrSpace(s.getClassName())) {
                    throw new Wtcc1001CException("不合法的抓取方案：处理的Java类必须设置");
                }
                try {
                    ((WebCrawler)Class.forName(s.getClassName()).newInstance()).toString();
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    throw new Wtcc1001CException("不合法的抓取方案：处理的Java必须是edu.uci.ics.crawler4j.crawler.WebCrawler的子类，所设置的类["+s.getClassName()+"]不合法", e);
                }
                retl.add(s);
            } catch(Exception e) {
                logger.error("创建抓取方案对象失败：{}", e.getClass().getName()+"::"+e.getMessage());
            }
        }
        return retl;
    }

    /**
     * 更改方案
     * @param s
     */
    public void updateScheme(Scheme s) {
        SchemePo sPo=s.convert2Po();
        schemeDao.update("updateScheme", sPo);
    }

    /**
     * 向数据库插入新的抓取批次
     * @param cBatch 抓取批次
     */
    public void insertBatch(CrawlBatch cBatch) {
        batchDao.insert("insertBatch", cBatch.convert2Po());
    }

    /**
     * 获得抓取批次
     * @param id 方案Id
     * @param curNum 当前的批次号
     * @return 对应的批次信息
     */
    public CrawlBatch getBatch(String id, int curNum) {
        Map<String, Object> keyM=new HashMap<String, Object>();
        keyM.put("schemeId", id);
        keyM.put("schemeNum", curNum);
        CrawlBatchPo bPo=batchDao.getInfoObject("getBatchByKey", keyM);
        CrawlBatch ret=null;
        if (bPo!=null) {
            ret=new CrawlBatch();
            ret.buildFromPo(bPo);
        }
        return ret;
    }

    /**
     * 初始化批次数据中的已访问UrlMap，这个Map可能很大
     * @param id 方案Id
     * @param curNum 当前批次号
     * @param cBatch 批次数据，本处理的的返回值就在这个数据中
     */
    public void initVisitedUrl(CrawlBatch cBatch) {
        Map<String, Object> keyM=new HashMap<String, Object>();
        keyM.put("schemeId", cBatch.getScheme().getId());
        keyM.put("schemeNum", cBatch.getSchemeNum());
        keyM.put("orgiTable", cBatch.getScheme().getOrigTableName());
        long count=batchDao.getCount("orgiCount", keyM);
        long hasCount=0;
        int pageNumber=0, pageSize=1000;
        while (hasCount<count) {
            Page<Map<String,Object>> page = batchDao.pageQueryAutoTranform("orgiCount", "getSchemeBatchVisitedList", keyM, pageNumber, pageSize);
            for (Map<String,Object> one: page.getResult()) {
                cBatch.addVisitedUrl(one.get("visitUrl")+"");
            }
            pageNumber++;
            hasCount+=1000;
        }
    }

    /**
     * 结束批次的信息写入数据库
     * @param crawlBatch
     */
    public void finishBatch(CrawlBatch crawlBatch) {
        batchDao.update("finishedBatch", crawlBatch.convert2Po());
    }

    /**
     * 更新某方案当前运行批次的进度情况:为抓取
     * @param scheme 方案
     */
    public void updateBatchProgress4Fetch(Scheme scheme) {
        if (scheme==null||scheme.getCrawlBatch()==null) return;
        Map<String, Object> param=new HashMap<String, Object>();
        param=scheme.getCrawlBatch().convert2Po().toHashMap();
        param.put("orgiTable", scheme.getOrigTableName());
        param.put("duration", System.currentTimeMillis()-scheme.getCrawlBatch().getBeginTime().getTime());
        batchDao.update("batchProgress4Fetch", param);
    }
}