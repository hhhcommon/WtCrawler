package com.woting.crawler.scheme.XMLY;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.BaseObject;
import com.spiritdata.framework.core.model.Page;

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

    /**
     * 获取喜马拉雅抓取的原始数据
     * @param pageSize 每页数据量
     * @param pageNum 页数
     * @return
     */
    public List<Map<String, Object>> getOrigDataList(int pageSize, int pageNum) {
        Page<Map<String,Object>> page = xmlyDao.pageQueryAutoTranform("orgiCount", "getTobeDealData", null, pageNum, pageSize);
        return (List<Map<String, Object>>) page.getResult();
    }
}