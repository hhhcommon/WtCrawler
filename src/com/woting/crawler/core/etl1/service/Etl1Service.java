package com.woting.crawler.core.etl1.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.core.etl1.Etl1Process;
import com.woting.crawler.core.etl1.model.Etl1Config;
import com.woting.crawler.core.etl1.persis.po.Etl1ConfigPo;
import com.woting.crawler.exceptionC.Wtcc1002CException;

public class Etl1Service {
    private Logger logger = LoggerFactory.getLogger(Etl1Service.class);

    @Resource(name="defaultDAO")
    private MybatisDAO<Etl1ConfigPo> etl1Dao;

    @PostConstruct
    public void initParam() {
        etl1Dao.setNamespace("C_ETL1");
    }

    public List<Etl1Config> getEtl1ConfigsFromDB() {
        List<Etl1ConfigPo> asList = etl1Dao.queryForList("getAllEtl");
        if (asList==null||asList.isEmpty()) return null;
        List<Etl1Config> retl=new ArrayList<Etl1Config>();
        for (Etl1ConfigPo ePo: asList) {
            try {
                Etl1Config eConfig=new Etl1Config();
                eConfig.buildFromPo(ePo);
                if (StringUtils.isNullOrEmptyOrSpace(eConfig.getEtl1Name())) {
                    throw new Wtcc1002CException("不合法的Etl1配置：Etl1名称必须设置");
                }
                if (StringUtils.isNullOrEmptyOrSpace(eConfig.getClassName())) {
                    throw new Wtcc1002CException("不合法的Etl1配置：处理的Java类必须设置");
                }
                try {
                    ((Etl1Process)Class.forName(eConfig.getClassName()).newInstance()).toString();
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    throw new Wtcc1002CException("不合法的抓取方案：处理的Java类必须实现com.woting.crawler.core.etl1.Etl1Process接口，所设置的类["+eConfig.getClassName()+"]不合法", e);
                }
                retl.add(eConfig);
            } catch(Exception e) {
                logger.error("获得抓取方案对象失败：{}", e.getClass().getName()+"::"+e.getMessage());
            }
        }
        return retl;
    }
}