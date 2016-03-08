package com.woting.crawler.service;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.woting.crawler.core.persis.po.DictMasterPo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class TestService {
    @Resource(name="defaultDAO")
    private MybatisDAO<DictMasterPo> dictMDao;

    @PostConstruct
    public void initParam() {
        dictMDao.setNamespace("dMaster");
    }

    public void justTest() {
        System.out.println("Just Test");

        Map<String, Object> param=new HashMap<String, Object>();
        param.put("ownerId", "0");
        param.put("ownerType", "0");
        param.put("sortByClause", "id");
        List<DictMasterPo> _l = dictMDao.queryForList(param);
        System.out.println(_l.size());
    }

    public void insertTest(Map<String, Object> data) {
        try {
            dictMDao.insert("tempXMLY", data);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}