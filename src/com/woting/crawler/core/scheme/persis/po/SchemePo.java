package com.woting.crawler.core.scheme.persis.po;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 抓取方案的Po对象，与数据库表wt_c_Scheme字段一一对应
 * @author wanghui
 */
public class SchemePo extends BaseObject {
    private static final long serialVersionUID = -673004352123772483L;

    private String id;//抓取模版Id
    private String sourceId;//抓取源Id

    private int schemeType;//方案类型，1-文件导入；2数据库方式
    private String fileUrls;//若是文件方式，则是文件名称，(相同的方案不同文件名，在这里用空格隔开)

    private String schemeName;//方案名称
    private String schemeDescn;//方案描述

    private int isValidate=1;//若是文件方式，则是文件名称
    private int crawlType=1;//抓取循环类型；1=只抓取1次，n抓取n次；0一直循环下去
    private long intervalTime=0;//两次抓取之间的间隔时间，毫秒；<=0上次完成后，立即执行；>0上次执行完间隔的毫秒数
    private int processNum; //方案实际已经运行的次数

    //以下是crawl4j所需要的
    private int threadNum=1; //执行线程数:crawl4j
    private String className=null; //执行的抓取类:crawl4j
    private String fetchSeeds=null; //抓取内容种子URL，用空格隔开:crawl4j
    private String tempPath=null; //存储临时数据的本机操作系统路径，若为空，系统自动给出这个地址:crawl4j

    private int isStoreWeb; //是否存储网页：1存储；0不存储
    private String tempStorePath=null; //当isStoreWeb=1此字段才有意义， 网页内容存储的根路径，若为空，系统自动给出这个地址:crawl4j 
    private Timestamp CTime; //记录创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    public int getSchemeType() {
        return schemeType;
    }
    public void setSchemeType(int schemeType) {
        this.schemeType = schemeType;
    }
    public String getFileUrls() {
        return fileUrls;
    }
    public void setFileUrls(String fileUrls) {
        this.fileUrls = fileUrls;
    }
    public String getSchemeName() {
        return schemeName;
    }
    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }
    public String getSchemeDescn() {
        return schemeDescn;
    }
    public void setSchemeDescn(String schemeDescn) {
        this.schemeDescn = schemeDescn;
    }
    public int getCrawlType() {
        return crawlType;
    }
    public void setCrawlType(int crawlType) {
        this.crawlType = crawlType;
    }
    public long getIntervalTime() {
        return intervalTime;
    }
    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }
    public int getThreadNum() {
        return threadNum;
    }
    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String getFetchSeeds() {
        return fetchSeeds;
    }
    public void setFetchSeeds(String fetchSeeds) {
        this.fetchSeeds = fetchSeeds;
    }
    public int getProcessNum() {
        return processNum;
    }
    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }
    public String getTempPath() {
        return tempPath;
    }
    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }
    public int getIsStoreWeb() {
        return isStoreWeb;
    }
    public void setIsStoreWeb(int isStoreWeb) {
        this.isStoreWeb = isStoreWeb;
    }
    public String getTempStorePath() {
        return tempStorePath;
    }
    public void setTempStorePath(String tempStorePath) {
        this.tempStorePath = tempStorePath;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
    public int getIsValidate() {
        return isValidate;
    }
    public void setIsValidate(int isValidate) {
        this.isValidate = isValidate;
    }
}