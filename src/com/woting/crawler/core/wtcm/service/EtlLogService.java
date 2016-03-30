package com.woting.crawler.core.wtcm.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.woting.crawler.core.wtcm.persis.po.ResOrgAssetRefPo;

@Service
public class EtlLogService {
    @Resource(name="defaultDAO_CM")
    private MybatisDAO<ResOrgAssetRefPo> etlLogDao;

    @PostConstruct
    public void initParam() {
        etlLogDao.setNamespace("C_RESORGASSETREF");
    }

    public ResOrgAssetRefPo getExistRef(ResOrgAssetRefPo etlInfo) {
        return etlLogDao.getInfoObject("getExistRef", etlInfo.getId());
    }

    public void save(ResOrgAssetRefPo etlInfo) {
        etlLogDao.insert(etlInfo);
    }
}