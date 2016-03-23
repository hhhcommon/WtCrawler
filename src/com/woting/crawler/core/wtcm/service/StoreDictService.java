package com.woting.crawler.core.wtcm.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.core.model.tree.TreeNodeBean;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.common.model.Owner;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.service.DictService;
import com.woting.cm.core.tracklog.model.TrackLog;
import com.woting.cm.core.tracklog.service.TrackLogService;
import com.woting.crawler.CrawlerConstants;

@Lazy(true)
@Service
public class StoreDictService {
    public final static Object lock=new Object();

    @Resource
    private TrackLogService trackLogService;
    @Resource
    private DictService dictService;

    private _CacheDictionary _cd=null;
    private Map<String, Map<String, Object>> ownerDataLogMap=null;

    @PostConstruct
    public void initParam() {
        //初始化字典
        _cd = ((CacheEle<_CacheDictionary>)SystemCache.getCache(CrawlerConstants.CM_DICT)).getContent();
        //初始化字典源数据的日志记录
        ownerDataLogMap=new HashMap<String, Map<String, Object>>();
    }

    /**
     * 获得字典日志记录
     * @return 字典日志记录
     */
    public Map<String, Map<String, Object>> getOwnerDataLogMap() {
        return ownerDataLogMap;
    }
    /**
     * 保存数据
     * @param dictMid 字典组id
     * @param parentId 上级结点Id，若为"-1"则在根加入
     * @param Owner 源数据的所有者，如"喜马拉雅"，"荔枝"等
     * @param saveData 被保存的数据(源数据)，其中的数据要和DictDetail中的对上（但如下字段系统自动生成Id，Py等
     * @return -1处理异常,0没有处理,1插入,2修改,4仅确认;负数是问题退出:-2没有找到的父节点，无法插入
     */
    public int saveDict(String dictMid, String parentId, Owner o, Map<String, Object> saveData) {
        try {
            String nodeName=saveData.get("nodeName")+"";
            if (StringUtils.isNullOrEmptyOrSpace(nodeName)) return -1;

            int dataClass=(dictMid.equals("3")?1:(dictMid.equals("6")?2:0));
            String ownerKey=o.getKey();
            String bizKey=dictMid+"::"+parentId+"::"+nodeName;
            String dataMd5=DigestUtils.md5Hex(parentId+nodeName);

            DictDetail dd=null;
            int ret=0;
            synchronized (StoreDictService.lock) {
                //看是否已经有了
                DictModel dictM=_cd.getDictModelById(dictMid);
                TreeNode<DictDetail> findNode=null;
                TreeNode<? extends TreeNodeBean> tn=((parentId.equals("-1")||parentId==null)?dictM.dictTree.getRoot():dictM.dictTree.findNode(parentId));
                if (tn==null) return -2;
                if (tn!=null&&tn.getChildCount()>0) {
                    for (TreeNode<? extends TreeNodeBean> ctn: tn.getChildren()) {
                        if (ctn!=null&&ctn.getNodeName().equals(nodeName)) {
                            findNode=(TreeNode<DictDetail>)ctn;
                            break;
                        }
                    }
                }

                TrackLog tlog=null;
                boolean needWriteLog=false, needUpdateCache=false;
                Map<String, Object> dataLogMap=null;
                if (findNode!=null) {//如果找到了，不进行添加，看是否需要记录
                    ret=4;
                    //看log中是否有对应的记录，若没有加入一条，若有看是否是同一个所属源来的
                    dataLogMap=ownerDataLogMap.get(ownerKey);
                    if (dataLogMap==null) {
                        dataLogMap=new HashMap<String, Object>();
                        ownerDataLogMap.put(ownerKey, dataLogMap);
                    }
                    tlog=(TrackLog)dataLogMap.get(bizKey);
                    //从数据库中再取一次
                    if (tlog==null) {
                        tlog=trackLogService.getTLogByBizKey("plat_DictD", o, dataMd5);
                        needUpdateCache=true;
                    }
                    if (tlog!=null) {
                        if (tlog.getDealFlag()!=4) {
                            needWriteLog=true;
                            tlog.setDealFlag(4);
                        }
                        //仅确认(对比),过一周（7天）后再次确认
                        else if (System.currentTimeMillis()-tlog.getCTime().getTime()>24*60*60*1000*7) needWriteLog=true;
                    } else {
                        needWriteLog=true;
                        tlog=new TrackLog();
                        tlog.setTableName("plat_DictD");
                        tlog.setObjId(findNode.getId());
                        tlog.setData(nodeName);
                        tlog.setDataMd5(dataMd5);
                        tlog.setDealFlag(4);//仅做了对比
                        tlog.setRules("etl");//仅做了对比
                        tlog.setOwner(o);
                        tlog.setOper(new Owner(100, "etl"));
                        tlog.setDataClass(dataClass);
                    }
                } else {//如果没找到，就要进行插入
                    //构造新的TreeNode对象
                    dd=new DictDetail();
                    dd.setId(SequenceUUID.getUUIDSubSegment(4));
                    dd.setDdName(nodeName);
                    dd.setMId(dictMid);
                    dd.setBCode(dd.getNPy());
                    //插入树
                    TreeNode<DictDetail> ddNode=new TreeNode<DictDetail>(dd);
                    tn.addChild(ddNode);
                    if (tn.isRoot()) dd.setParentId("0");

                    tlog=new TrackLog();
                    tlog.setTableName("plat_DictD");
                    tlog.setObjId(dd.getId());
                    tlog.setData(nodeName);
                    tlog.setDataMd5(dataMd5);
                    tlog.setDealFlag(1);//仅做了对比
                    tlog.setRules("etl");//仅做了对比
                    tlog.setOwner(o);
                    tlog.setOper(new Owner(100, "etl"));
                    tlog.setDataClass(dataClass);
                    needWriteLog=true;
                    needUpdateCache=true;
                }
                if (needWriteLog) {
                    tlog.setId(SequenceUUID.getPureUUID());
                    trackLogService.insert(tlog);
                }
                if (needUpdateCache&&tlog!=null) {
                    if (dataLogMap==null) {
                        dataLogMap=new HashMap<String, Object>();
                        ownerDataLogMap.put(ownerKey, dataLogMap);
                    }
                    dataLogMap.put(bizKey, tlog);
                }
            } //END SYND

            //在同步块外再写入数据库，这样快
            //插入数据库
            if (dd!=null) {
                dictService.addDictDetail(dd);
                ret=1;
            }
            return ret;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}