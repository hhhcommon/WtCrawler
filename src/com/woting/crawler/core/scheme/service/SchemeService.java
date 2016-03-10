package com.woting.crawler.core.scheme.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.BaseObject;

@Service
public class SchemeService {
    @Resource(name="defaultDAO")
    private MybatisDAO<BaseObject> schemeDao;

    @PostConstruct
    public void initParam() {
        schemeDao.setNamespace("dMaster");
    }
}