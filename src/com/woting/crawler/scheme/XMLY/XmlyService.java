package com.woting.crawler.scheme.XMLY;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.BaseObject;

@Service
public class XmlyService {
    @Resource(name="defaultDAO")
    private MybatisDAO<BaseObject> xmlyDao;

    @PostConstruct
    public void initParam() {
        xmlyDao.setNamespace("C_XMLY");
    }

    /**
     * 原始表数据加入
     * @param parseData
     */
    public void insertOrig(Map<String, Object> parseData) {
        xmlyDao.insert("insertOrgi", parseData);
    }
}