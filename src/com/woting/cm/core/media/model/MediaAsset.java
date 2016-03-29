package com.woting.cm.core.media.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.perimeter.model.Organize;

public class MediaAsset implements Serializable, ModelSwapPo {
    private static final long serialVersionUID=-3052538880958574852L;

    private String id; //uuid(主键)
    private String maTitle; //媒体资源名称
    private int maPubType; //发布者记录类型：1-组织表,2-文本
    private String maPubId; //发布者所属组织Id，对应表wt_Organize内容
    private String maPublisher; //发布者名称
    private Timestamp maPublishTime; //发布时间
    private String maImg; //媒体图Url
    private String maURL; //媒体主地址，可以是聚合的源，也可以是Wt平台中的文件URL
    private String subjectWords; //主题词
    private String keyWords; //关键词
    private long timeLong; //时长，毫秒数
    private String descn; //说明
    private int pubCount=0; //发布状态：0未发布;>0被发布到多少个栏目中（系列节目的发布，这里的单曲也要被加1）
    private Timestamp CTime; //记录创建时间

    private Organize publisher; //发布者类型，比如逻辑思维团队
    private DictDetail lang; //发布语言，字典项
    private List<SeqMediaAsset> seqMaList; //所对应的专辑列表
    private List<MaSource> maSourceList; //对应的音频资源列表

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
    public Timestamp getMaPublishTime() {
        return maPublishTime;
    }
    public void setMaPublishTime(Timestamp maPublishTime) {
        this.maPublishTime=maPublishTime;
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
        if (seqMa==null) return;
        if (seqMaList==null) {
            seqMaList=new ArrayList<SeqMediaAsset>();
            seqMaList.add(seqMa);
        } else {
            boolean canAdd=true;
            for (SeqMediaAsset sma: seqMaList) {
                if (sma.equals(seqMa)) {
                    canAdd=false;
                    break;
                }
            }
            if (canAdd) seqMaList.add(seqMa);
        }
        //计算发布Channel
        
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
    public List<MaSource> getMaSourceList() {
        return maSourceList;
    }
    public void setMaSourceList(List<MaSource> maSourceList) {
        this.maSourceList=maSourceList;
        if (maSourceList!=null&&!maSourceList.isEmpty()) {
            for (MaSource mas: maSourceList) {
                if (mas.isMain()) {
                    this.maURL=mas.getPlayURI();
                    break;
                }
            }
        }
    }
    public void addMaSource(MaSource maSrc) {
        if (maSourceList==null) maSourceList=new ArrayList<MaSource>();
        boolean canAdd=true;
        for (MaSource mas: maSourceList) {
            if (mas.equals(maSrc)) {
                canAdd=false;
                break;
            }
        }
        if (canAdd) {
            maSourceList.add(maSrc);
            if (maSrc.isMain()) this.maURL=maSrc.getPlayURI();
        }
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
        ret.setMaPublishTime(maPublishTime);
        ret.setMaImg(maImg);
        ret.setMaURL(maURL);
        if (maSourceList!=null&&!maSourceList.isEmpty()) {
            for (MaSource mas: maSourceList) {
                if (mas.isMain()) {
                    this.maURL=mas.getPlayURI();
                    break;
                }
            }
        }
        ret.setSubjectWords(subjectWords);
        ret.setKeyWords(keyWords);
        ret.setLangDid(lang.getId());
        ret.setLanguage(lang.getNodeName());
        ret.setDescn(descn);
        ret.setCTime(CTime);
        ret.setPubCount(pubCount);

        return ret;
    }

    @Override
    public void buildFromPo(Object po) {
        if (po==null) throw new Plat0006CException("Po对象为空，无法从空对象得到概念/逻辑对象！");
        if (!(po instanceof MediaAssetPo)) throw new Plat0006CException("Po对象不是MediaAssetPo的实例，无法从此对象构建单体节目对象！");
        MediaAssetPo _po=(MediaAssetPo)po;

        this.id=_po.getId();
        this.maTitle=_po.getMaTitle();
        //对应发布者——没有办法现在直接处理，先只把值记录下来
        this.maPubType=_po.getMaPubType();
        if (this.maPubType==1) {
            this.publisher=new Organize();
            this.publisher.setId(_po.getMaPubId());
            this.publisher.setOrgName(_po.getMaPublisher());
        } else {
            this.maPubId=_po.getMaPubId();
            this.maPublisher=_po.getMaPublisher();
        }
        this.maPublishTime=_po.getMaPublishTime();
        this.maImg=_po.getMaImg();
        this.maURL=_po.getMaURL();
        this.subjectWords=_po.getSubjectWords();
        this.keyWords=_po.getKeyWords();
        //组织分类字典——没有办法现在直接处理，先只把值记录下来
        DictDetail dd=new DictDetail();
        dd.setId(_po.getLangDid());
        dd.setNodeName(_po.getLanguage());
        this.lang=dd;
        this.timeLong=_po.getTimeLong();
        this.descn=_po.getDescn();
        this.pubCount=_po.getPubCount();
        this.CTime=_po.getCTime();

        //所属专辑、发布栏目、声音源等信息无法从这里得到
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null||!(o instanceof MediaAsset)) return false;

        MediaAsset _o=(MediaAsset)o;
        if (_o.getId().equals(id)) return true;
        return false;
    }
}