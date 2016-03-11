package com.woting.crawler.core.scheme.model;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.woting.crawler.core.scheme.persis.po.CrawlBatchPo;

public class CrawlBatch implements ModelSwapPo {
    private int schemeNum; //该方案下的序号
    private Timestamp beginTime; //方案执行开始时间
    private Timestamp endTime; //方案执行结束时间
    private int duration; //执行总时间，毫秒数
    private int visitCount; //总访问网页数
    private int insertCount; //插入记录数
    private int updateCount; //更新记录数
    private int delCount; //删除记录数
    private int flag; //抓取状态：0正在抓取；1抓取完成

    private Scheme scheme; //抓取方案，这个批处理抓取任务所依据的方案

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
    public Scheme getScheme() {
        return scheme;
    }
    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    @Override
    public void buildFromPo(Object po) {
        if (po==null) throw new Plat0006CException("Po对象为空，无法从空对象得到概念/逻辑对象！");
        if (!(po instanceof CrawlBatchPo)) throw new Plat0006CException("Po对象不是CrawlBatchPo的实例，无法从此对象构建处理记录对象！");
        CrawlBatchPo _po = (CrawlBatchPo)po;
        this.setSchemeNum(_po.getSchemeNum());
        this.setBeginTime(_po.getBeginTime());
        this.setEndTime(_po.getEndTime());
        this.setDuration(_po.getDuration());
        this.setVisitCount(_po.getVisitCount());
        this.setInsertCount(_po.getInsertCount());
        this.setUpdateCount(_po.getUpdateCount());
        this.setDelCount(_po.getDelCount());
        this.setFlag(_po.getFlag());
        //scheme不能处理
    }

    @Override
    public CrawlBatchPo convert2Po() {
        CrawlBatchPo ret = new CrawlBatchPo();
        ret.setSchemeId(this.getScheme()==null?null:this.getScheme().getId());
        ret.setSchemeNum(this.getSchemeNum());
        ret.setBeginTime(this.getBeginTime());
        ret.setEndTime(this.getEndTime());
        ret.setDuration(this.getDuration());
        ret.setVisitCount(this.getVisitCount());
        ret.setInsertCount(this.getInsertCount());
        ret.setUpdateCount(this.getUpdateCount());
        ret.setDelCount(this.getDelCount());
        ret.setFlag(this.getFlag());
        return ret;
    }
}