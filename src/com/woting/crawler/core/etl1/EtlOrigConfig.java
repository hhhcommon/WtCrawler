package com.woting.crawler.core.etl1;

/**
 * 原始数据处理的配置
 * @author wanghui
 */
public class EtlOrigConfig {
    private String etlName;//原始数据处理名称
    private int threadNum=5;//每条数据的处理线程的个数
    private String loadOrigClassName;//加载原始数据的类
    private String schemeId;//抓取方案Id
    private int cleanInterval=1000;//检查清除内存的时间隔时间,1秒

    public int getThreadNum() {
        return threadNum;
    }
    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public String getLoadOrigClassName() {
        return loadOrigClassName;
    }

    public String getEtlName() {
        return etlName;
    }
    public void setEtlName(String etlName) {
        this.etlName = etlName;
    }

    public String getSchemeId() {
        return schemeId;
    }
    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public int getCleanInterval() {
        return cleanInterval;
    }
    public void setCleanInterval(int cleanInterval) {
        this.cleanInterval = cleanInterval;
    }
}