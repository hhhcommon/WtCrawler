package com.woting.crawler.core.etl1.model;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.core.etl1.persis.po.Etl1ConfigPo;
import com.woting.crawler.exceptionC.Wtcc1002CException;

public class Etl1Config implements ModelSwapPo {
    private static final long serialVersionUID = -2128620688164591754L;

    private String id;//Etl1过程Id
    private String etl1Name;//etl名称
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
    public void setEtl1Name(String etl1Name) {
        this.etl1Name = etl1Name;
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

    @Override
    public void buildFromPo(Object po) {
        if (po==null) throw new Plat0006CException("Po对象为空，无法从空对象得到概念/逻辑对象！");
        if (!(po instanceof Etl1ConfigPo)) throw new Plat0006CException("Po对象不是Etl1ConfigPo的实例，无法从此对象构建Etl1配置对象！");

        Etl1ConfigPo _po = (Etl1ConfigPo)po;
        if (_po.getIsValidate()!=1) throw new Wtcc1002CException("无效Etl配置，无须转换");

        this.setId(_po.getId());
        this.setEtl1Name(_po.getEtl1Name());
        this.setThreadNum(_po.getThreadNum());
        this.setQueueSize(_po.getQueueSize());
        this.setClassName(_po.getClassName());
        this.setCTime(_po.getCTime());
    }

    @Override
    public Etl1ConfigPo convert2Po() {
        Etl1ConfigPo ret = new Etl1ConfigPo();
        ret.setId(StringUtils.isNullOrEmptyOrSpace(this.getId())?SequenceUUID.getUUIDSubSegment(4):this.getId());
        ret.setEtlName(this.getEtl1Name());
        ret.setIsValidate(1);
        ret.setThreadNum(this.getThreadNum());
        ret.setQueueSize(this.getQueueSize());
        ret.setClassName(this.getClassName());
        ret.setCTime(this.getCTime());
        return ret;
    }
}
