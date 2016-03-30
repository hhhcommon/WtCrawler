package com.woting.crawler.core.wtcm.persis.po;

import java.sql.Timestamp;

import org.apache.commons.codec.digest.DigestUtils;

import com.spiritdata.framework.core.model.BaseObject;

public class ResOrgAssetRefPo extends BaseObject {
    private static final long serialVersionUID = 3161628847599429462L;

    private String idxMd5; //用于索引的Md5码：resTableName+refType+orgId+origType+origId
    private String resTableName; //资源表名称
    private String resId; //表内记录Id;
    private String refType; //关联名称
    private String orgId; //外部系统Id,对应wt_Organize
    private String origType; //外部系统中主对象的分类，各个源不相同，有自己的规则
    private String origId; //外部系统中主对象的ID
    private String contentMd5; //内容Md5,这个内容是以Wt资源库为准的
    private Timestamp CTime; //记录创建时间

    public String getResTableName() {
        return resTableName;
    }
    public void setResTableName(String resTableName) {
        this.resTableName = resTableName;
    }
    public String getResId() {
        return resId;
    }
    public void setResId(String resId) {
        this.resId = resId;
    }
    public String getRefType() {
        return refType;
    }
    public void setRefType(String refType) {
        this.refType = refType;
    }
    public String getOrgId() {
        return orgId;
    }
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    public String getOrigType() {
        return origType;
    }
    public void setOrigType(String origType) {
        this.origType = origType;
    }
    public String getOrigId() {
        return origId;
    }
    public void setOrigId(String origId) {
        this.origId = origId;
    }
    public String getIdxMd5() {
        return idxMd5;
    }
    public void setIdxMd5(String idxMd5) {
        this.idxMd5 = idxMd5;
    }
    public String getContentMd5() {
        return contentMd5;
    }
    public void setContentMd5(String contentMd5) {
        this.contentMd5 = contentMd5;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }

    /**
     * 根据已有的信息生成id，从标识串所生成的Md5码
     * @return id
     */
    public String getId() {
        if (idxMd5==null) idxMd5=DigestUtils.md5Hex(resTableName+refType+orgId+origType+origId);
        return idxMd5;
    }
}