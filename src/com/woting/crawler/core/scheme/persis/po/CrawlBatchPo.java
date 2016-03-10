package com.woting.crawler.core.scheme.persis.po;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 抓取批次的Po对象，与数据库表wt_c_Batch字段一一对应
 * @author wanghui
 */
public class CrawlBatchPo extends BaseObject {
    private static final long serialVersionUID = -7795359226411975864L;

    private String schemeId; //方案Id
    private int schemeNum; //该方案下的序号
    private Timestamp beginTime; //方案执行开始时间
    private Timestamp endTime; //方案执行结束时间
    private int duration; //执行总时间，毫秒数
    private int visitCount; //总访问网页数
    private int insertCount; //插入记录数
    private int updateCount; //更新记录数
    private int delCount; //删除记录数
    private int flag; //抓取状态：0正在抓取；1抓取完成

    public String getSchemeId() {
        return schemeId;
    }
    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }
    public int getSchemeNum() {
        return schemeNum;
    }
    public void setSchemeNum(int schemeNum) {
        this.schemeNum = schemeNum;
    }
    public Timestamp getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
    }
    public Timestamp getEndTime() {
        return endTime;
    }
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getVisitCount() {
        return visitCount;
    }
    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
    public int getInsertCount() {
        return insertCount;
    }
    public void setInsertCount(int insertCount) {
        this.insertCount = insertCount;
    }
    public int getUpdateCount() {
        return updateCount;
    }
    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }
    public int getDelCount() {
        return delCount;
    }
    public void setDelCount(int delCount) {
        this.delCount = delCount;
    }
    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }
}