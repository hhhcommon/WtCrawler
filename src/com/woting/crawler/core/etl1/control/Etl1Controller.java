package com.woting.crawler.core.etl1.control;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.core.etl1.model.Etl1Config;
import com.woting.crawler.core.etl1.service.Etl1Service;
import com.woting.crawler.ext.SpringShell;

public class Etl1Controller {
    private Logger logger = LoggerFactory.getLogger(Etl1Controller.class);

    private List<Etl1Config> etl1List;//第一次Etl过程的配置信息列表

    /**
     * Etl1配置列表加载
     * @param loadType 加载类型:1-数据库方式;2-文件方式;
     * @param etl1Files 若是文件方式，这里是各Etl1过程的配置文件名，以双分号隔开
     */
    public void loadEtl1(int loadType, String[] etl1Files) {
        etl1List=new ArrayList<Etl1Config>();
        if (loadType==1) { //数据库方式
            Etl1Service es = (Etl1Service)SpringShell.getBean("etl1Service");
            etl1List=es.getEtl1ConfigsFromDB();
        } else { //文件方式
        }
        if (etl1List==null||etl1List.isEmpty()) logger.info("无可用的第一次Etl过程");
        else {
            String etl1Names="";
            for (Etl1Config ec: etl1List) {
                etl1Names+=","+ec.getEtl1Name();
            }
            logger.info("加载第一次Etl过程<{}>个，为[{}]", etl1List.size(), etl1Names.substring(1));
        }
    }

    public void runningScheme() {
        if (etl1List!=null&&!etl1List.isEmpty()) {
            for (Etl1Config eConfig: etl1List) {
                try {
                    Etl1Running etlRun=new Etl1Running(eConfig);
                    etlRun.start();
                } catch(Exception e) {
                    logger.error("启动第一次Etl处理过程[{}]异常：{}", StringUtils.isNullOrEmptyOrSpace(eConfig.getEtl1Name())?"无名":eConfig.getEtl1Name(), e.getClass().getName()+"::"+e.getMessage());
                }
            }
        }
    }
}
