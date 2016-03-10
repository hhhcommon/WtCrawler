package com.woting.crawler.core.scheme.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.core.scheme.persis.pojo.SourcePo;

/**
 * 内容源
 * @author wanghui
 */
public class Source implements ModelSwapPo {
    private String id;//内容源Id
    private String sourceName; //内容源名称
    private String sourceWeb; //主站站点URL，用空格隔开
    private Timestamp CTime; //记录创建时间

    private List<Scheme> subSchemes; //以此为源的所有下级模式

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

    @Override
    public void buildFromPo(Object po) {
        if (po==null) throw new Plat0006CException("Po对象为空，无法从空对象得到概念/逻辑对象！");
        if (!(po instanceof SourcePo)) throw new Plat0006CException("Po对象不是SourcePo的实例，无法从此对象构建内容源对象！");
        SourcePo _po = (SourcePo)po;
        this.setId(_po.getId());
        this.setSourceName(_po.getSourceName());
        this.setSourceWeb(_po.getSourceWeb());
        this.setCTime(_po.getCTime());
   }
    @Override
    public SourcePo convert2Po() {
        SourcePo ret = new SourcePo();
        ret.setId(StringUtils.isNullOrEmptyOrSpace(this.getId())?SequenceUUID.getUUIDSubSegment(4):this.getId());
        ret.setSourceName(this.getSourceName());
        ret.setSourceWeb(this.getSourceWeb());
        ret.setCTime(this.getCTime());
        return ret;
    }

    //下级模式处理
    public List<Scheme> getSubSchemes() {
        return subSchemes;
    }
    public void addSchemes(Scheme scheme) {
        if (scheme!=null&&scheme.getSourceName().equals(this.sourceName)) {
            if (this.subSchemes==null) this.subSchemes=new ArrayList<Scheme>();
            this.subSchemes.add(scheme);
        }
    }
}