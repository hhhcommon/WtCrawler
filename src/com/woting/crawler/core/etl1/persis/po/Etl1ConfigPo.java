package com.woting.crawler.core.etl1.persis.po;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

public class Etl1ConfigPo extends BaseObject {
    private static final long serialVersionUID = 6053391107961005709L;

    private String id;//Etl1过程Id
    private String etl1Name;//etl名称
    private int isValidate;//是否生效：1生效;2无效
    private int threadNum=1; //处理线程数
    private int queueSize=10000; //处理队列长度
    private String className=null; //处理类名称
    private Timestamp CTime; //记录创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEtl1Name() {
        return etl1Name;
    }
    public void setEtlName(String etl1Name) {
        this.etl1Name = etl1Name;
    }
    public int getIsValidate() {
        return isValidate;
    }
    public void setIsValidate(int isValidate) {
        this.isValidate = isValidate;
    }
    public int getThreadNum() {
        return threadNum;
    }
    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }
    public int getQueueSize() {
        return queueSize;
    }
    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
}