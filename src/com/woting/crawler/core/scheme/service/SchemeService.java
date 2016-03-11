package com.woting.crawler.core.scheme.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.woting.crawler.core.scheme.model.Scheme;
import com.woting.crawler.core.scheme.persis.po.CrawlBatchPo;
import com.woting.crawler.core.scheme.persis.po.SchemePo;
import com.woting.crawler.core.scheme.persis.po.SourcePo;

@Service
public class SchemeService {
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
        return null;
    }
}