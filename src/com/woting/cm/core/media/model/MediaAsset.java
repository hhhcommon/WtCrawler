package com.woting.cm.core.media.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.media.persis.po.MaSourcePo;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.perimeter.model.Organize;

public class MediaAsset implements Serializable, ModelSwapPo {
    private static final long serialVersionUID=-3052538880958574852L;

    private String id; //uuid(主键)
    private String maTitle; //媒体资源名称
    private int maPubType; //发布者记录类型：1-组织表,2-文本
    private String maPubId; //发布者所属组织Id，对应表wt_Organize内容
    private String maPublisher; //发布者名称
    private Timestamp maPublisherTime; //发布时间
    private String maImg; //媒体图Url
    private String maURL; //媒体主地址，可以是聚合的源，也可以是Wt平台中的文件URL
    private String subjectWords; //主题词
    private String keyWords; //关键词
    private long timeLong; //时长，毫秒数
    private String descn; //说明
    private int pubCount; //发布状态：0未发布;>0被发布到多少个栏目中（系列节目的发布，这里的单曲也要被加1）
    private Timestamp CTime; //记录创建时间

    private List<SeqMediaAsset> seqMaList; //所对应的专辑列表
    private List<Channel> pubChannelList; //发布到的栏目列表，包括直接发布和间接发布两种
    private DictDetail lang; //发布语言，字典项
    private Organize publisher; //发布者类型，比如逻辑思维团队

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id=id;
    }
    public String getMaTitle() {
        return maTitle;
    }
    public void setMaTitle(String maTitle) {
        this.maTitle=maTitle;
    }
    public int getMaPubType() {
        return maPubType;
    }
    public void setMaPubType(int maPubType) {
        this.maPubType=maPubType;
    }
    public String getMaPubId() {
        return maPubId;
    }
    public void setMaPubId(String maPubId) {
        this.maPubId=maPubId;
    }
    public String getMaPublisher() {
        return maPublisher;
    }
    public void setMaPublisher(String maPublisher) {
        this.maPublisher=maPublisher;
    }
    public Timestamp getMaPublisherTime() {
        return maPublisherTime;
    }
    public void setMaPublisherTime(Timestamp maPublisherTime) {
        this.maPublisherTime=maPublisherTime;
    }
    public String getMaImg() {
        return maImg;
    }
    public void setMaImg(String maImg) {
        this.maImg=maImg;
    }
    public String getMaURL() {
        return maURL;
    }
    public void setMaURL(String maURL) {
        this.maURL=maURL;
    }
    public String getSubjectWords() {
        return subjectWords;
    }
    public void setSubjectWords(String subjectWords) {
        this.subjectWords=subjectWords;
    }
    public String getKeyWords() {
        return keyWords;
    }
    public void setKeyWords(String keyWords) {
        this.keyWords=keyWords;
    }
    public long getTimeLong() {
        return timeLong;
    }
    public void setTimeLong(long timeLong) {
        this.timeLong=timeLong;
    }
    public String getDescn() {
        return descn;
    }
    public void setDescn(String descn) {
        this.descn=descn;
    }
    public int getPubCount() {
        return pubCount;
    }
    public void setPubCount(int pubCount) {
        this.pubCount=pubCount;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime=cTime;
    }

    public List<SeqMediaAsset> getSeqMaList() {
        return seqMaList;
    }
    public void setSeqMaList(List<SeqMediaAsset> seqMaList) {
        this.seqMaList=seqMaList;
    }
    public void addSeqMa(SeqMediaAsset seqMa) {
        if (seqMaList==null) seqMaList=new ArrayList<SeqMediaAsset>();
        boolean canAdd=true;
        for (SeqMediaAsset sma: seqMaList) {
            if (sma.equals(seqMa)) {
                canAdd=false;
                break;
            }
        }
        if (canAdd) seqMaList.add(seqMa);
    }
    public List<Channel> getPubChannelList() {
        return pubChannelList;
    }
    public void setPubChannelList(List<Channel> pubChannelList) {
        this.pubChannelList=pubChannelList;
    }
    public void addChannel(Channel c) {
        if (pubChannelList==null) pubChannelList=new ArrayList<Channel>();
        boolean canAdd=true;
        for (Channel _c: pubChannelList) {
            if (_c.equals(c)) {
                canAdd=false;
                break;
            }
        }
        if (canAdd) pubChannelList.add(c);
    }
    public DictDetail getLang() {
        return lang;
    }
    public void setLang(DictDetail lang) {
        this.lang=lang;
    }
    public Organize getPublisher() {
        return publisher;
    }
    public void setPublisher(Organize publisher) {
        this.publisher=publisher;
        maPubType=1;
        maPubId=publisher.getId();
        maPublisher=publisher.getOrgName();
    }

    @Override
    public MediaAssetPo convert2Po() {
        MediaAssetPo ret=new MediaAssetPo();

        //id处理，没有id，自动生成一个
        if (StringUtils.isNullOrEmptyOrSpace(this.id)) ret.setId(SequenceUUID.getUUIDSubSegment(4));
        else ret.setId(this.id);

        ret.setMaTitle(maTitle);
        if (publisher!=null) {
            ret.setMaPubType(1);
            ret.setMaPubId(publisher.getId());
            ret.setMaPublisher(publisher.getOrgName());
        } else {
            ret.setMaPubType(2);
            ret.setMaPubId(maPubId);
            ret.setMaPublisher(maPublisher);
        }
        ret.setMaPublisherTime(maPublisherTime);
        ret.setMaImg(maImg);
        ret.setMaURL(maURL);
        ret.setSubjectWords(subjectWords);
        ret.setKeyWords(keyWords);
        ret.setLangDid(lang.getId());
        ret.setLanguage(lang.getNodeName());
        ret.setDescn(descn);
        ret.setCTime(this.CTime);

        return ret;
    }

    @Override
    public void buildFromPo(Object po) {
        if (po==null) throw new Plat0006CException("Po对象为空，无法从空对象得到概念/逻辑对象！");
        if (!(po instanceof MediaAssetPo)) throw new Plat0006CException("Po对象不是MediaAssetPo的实例，无法从此对象构建单体节目对象！");
        MediaAssetPo _po=(MediaAssetPo)po;

        
    }
}