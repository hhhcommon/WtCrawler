package com.woting.crawler.core.scheme.persis.pojo;

import java.sql.Timestamp;

/**
 * 内容源网站信息的Po对象，与数据库表wt_c_Source字段一一对应
 * @author wanghui
 */
public class SourcePo {
    private String id;//内容源id
    private String sourceName; //内容源名称
    private String sourceWeb; //主站站点URL，用空格隔开
    private Timestamp CTime; //记录创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSourceName() {
        return sourceName;
    }
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    public String getSourceWeb() {
        return sourceWeb;
    }
    public void setSourceWeb(String sourceWeb) {
        this.sourceWeb = sourceWeb;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
}