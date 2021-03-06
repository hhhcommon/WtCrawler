package com.woting.crawler.core.scheme.control;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.core.scheme.model.Scheme;
import com.woting.crawler.core.scheme.service.SchemeService;
import com.woting.crawler.ext.SpringShell;

/**
 * 主控制程序
 * @author wanghui
 */
public class SchemeController {
    private Logger logger = LoggerFactory.getLogger(SchemeController.class);

    private List<Scheme> activeSchemes;//活动的可处理的方案

    /**
     * 方案列表加载
     * @param loadType 加载类型:1-数据库方式;2-文件方式;
     * @param schemeFiles 若是文件方式，这里是各方案的配置文件名
     */
    public void loadScheme(int loadType, String[] schemeFiles) {
        activeSchemes=new ArrayList<Scheme>();

        if (loadType==1) { //数据库方式
            SchemeService ss = (SchemeService)SpringShell.getBean("schemeService");
            activeSchemes=ss.getActiveSchemesFromDB();
        } else { //文件方式
        }
        if (activeSchemes==null||activeSchemes.isEmpty()) logger.info("无可用方案");
        else {
            String schemeNames="";
            for (Scheme s: activeSchemes) {
                schemeNames+=","+s.getSchemeName();
            }
            logger.info("加载方案<{}>个，为[{}]", activeSchemes.size(), schemeNames.substring(1));
        }
    }

    public void runningScheme() {
        if (activeSchemes!=null&&!activeSchemes.isEmpty()) {
            for (Scheme s: activeSchemes) {
                try {
                    SchemeMoniter sm=new SchemeMoniter(s);
                    sm.start();
                } catch(Exception e) {
                    logger.error("启动方案[{}]异常：{}", StringUtils.isNullOrEmptyOrSpace(s.getSchemeName())?"无名":s.getSchemeName(), e.getClass().getName()+"::"+e.getMessage());
                }
            }
        }
    }
}