package com.woting.crawler.core.scheme.model;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.core.scheme.persis.pojo.SchemePo;
import com.woting.crawler.exceptionC.Wtcc1000CException;

/**
 * 抓取模式。注意模式名称和抓取源不能同时相等
 * @author wanghui
 */
public class Scheme implements ModelSwapPo {
    private String id;//抓取模版Id
    private String schemeName;//抓取模版的名称
    private String schemeDescn;//模版描述
    private Source source;//抓取源

    private int crawlType=1;//抓取循环类型；1=只抓取1次，n抓取n次；0一直循环下去
    private long intervalTime=0;//两次抓取之间的间隔时间，毫秒；<=0上次完成后，立即执行；>0上次执行完间隔的毫秒数

    //以下是crawl4j所需要的
    private int threadNum=1; //执行线程数:crawl4j
    private String className=null; //执行的抓取类:crawl4j
    private String fetchSeeds=null; //抓取内容种子URL，用空格隔开:crawl4j
    private int processNum; //模式实际已经运行的次数，初始次数
    private String tempPath=null; //存储临时数据的本机操作系统路径，若为空，系统自动给出这个地址:crawl4j
    private int isStoreWeb; //是否存储网页：1存储；0不存储
    private String tempStorePath=null; //当isStoreWeb=1此字段才有意义， 网页内容存储的根路径，若为空，系统自动给出这个地址:crawl4j 

    private Timestamp CTime; //记录创建时间

    private CrawlBatch crawlBatch; //该模式下正在处理的处理内容

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Source getSource() {
        return source;
    }
    public void setSource(Source source) {
        this.source = source;
    }
    public String getSourceName() {
        if (this.source==null) return null;
        return this.source.getSourceName();
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
    public CrawlBatch getCrawlBatch() {
        return crawlBatch;
    }
    public void setCrawlBatch(CrawlBatch crawlBatch) {
        if (crawlBatch!=null) {
            if (crawlBatch.getScheme()==null) {
                this.crawlBatch=crawlBatch;
                crawlBatch.setScheme(this);
            } else {
                if (!crawlBatch.getScheme().getId().equals(this.getId())) throw new Wtcc1000CException("抓取处理记录与模式不匹配");
                this.crawlBatch=crawlBatch;
            }
        }
    }

    /**
     * 判断两个模式Key是否相同(模式名称+源名称)
     * @param other 另一个模式
     * @return 相同返回true，否则返回false;
     */
    public boolean keyEquals(Object other) {
        if (this==other) return true;
        if ((other==null)||(getClass()!=other.getClass())) return false;
        Scheme _other = (Scheme)other;
        if (!this.schemeName.equals(_other.getSchemeName())) return false;
        return this.getSourceName().equals(_other.getSourceName());
    }

    /**
     * 判断两个模式的处理是否相同（抓取类，抓取类型，抓取种子）
     * @param other 另一个模式
     * @return 相同返回true，否则返回false;
     */
    public boolean processEquals(Object other) {
        if (this==other) return true;
        if ((other==null)||(getClass()!=other.getClass())) return false;
        Scheme _other = (Scheme)other;
        return (this.getClassName().equals(_other.getClassName())
              &&this.getFetchSeeds().equals(_other.getFetchSeeds())
              &&this.getCrawlType()==_other.getCrawlType());
    }

    @Override
    public void buildFromPo(Object po) {
        if (po==null) throw new Plat0006CException("Po对象为空，无法从空对象得到概念/逻辑对象！");
        if (!(po instanceof SchemePo)) throw new Plat0006CException("Po对象不是SchemePo的实例，无法从此对象构建抓取模式对象！");
        SchemePo _po = (SchemePo)po;
        if (!(po instanceof SchemePo)) throw new Plat0006CException("Po对象不是SchemePo的实例，无法从此对象构建抓取模式对象！");

        this.setId(_po.getId());
        this.setSchemeName(_po.getSchemeName());
        this.setSchemeDescn(_po.getSchemeDescn());
        this.setCrawlType(_po.getCrawlType());
        this.setIntervalTime(_po.getIntervalTime());
        this.setClassName(_po.getClassName());
        this.setThreadNum(_po.getThreadNum());
        this.setFetchSeeds(_po.getFetchSeeds());
        this.setProcessNum(_po.getProcessNum());
        this.setTempPath(_po.getTempPath());
        this.setIsStoreWeb(_po.getIsStoreWeb());
        this.setTempStorePath(_po.getTempStorePath());
        this.setCTime(_po.getCTime());

        //source不处理
        //_po.getSourceId();
    }
    @Override
    public SchemePo convert2Po() {
        SchemePo ret = new SchemePo();
        ret.setId(StringUtils.isNullOrEmptyOrSpace(this.getId())?SequenceUUID.getUUIDSubSegment(4):this.getId());
        ret.setSourceId(this.getSource()==null?null:this.getSource().getId());
        ret.setSchemeName(this.getSchemeName());
        ret.setSchemeDescn(this.getSchemeDescn());
        ret.setCrawlType(this.getCrawlType());
        ret.setIsValidate(1);
        ret.setIntervalTime(this.getIntervalTime());
        ret.setClassName(this.getClassName());
        ret.setThreadNum(this.getThreadNum());
        ret.setFetchSeeds(this.getFetchSeeds());
        ret.setProcessNum(this.getProcessNum());
        ret.setTempPath(this.getTempPath());
        ret.setIsStoreWeb(this.getIsStoreWeb());
        ret.setTempStorePath(this.getTempStorePath());
        ret.setCTime(this.getCTime());
        return ret;
        //以下无法处理
//        private int schemeType;//模式类型，1-文件导入；2数据库方式
//        private String fileUrls;//若是文件方式，则是文件名称，(相同的模式不同文件名，在这里用空格隔开)
    }
}