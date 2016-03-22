package com.woting.crawler.core.wtcm.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.core.model.tree.TreeNodeBean;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.common.model.Owner;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.service.DictService;
import com.woting.cm.core.tracklog.model.TrackLog;
import com.woting.cm.core.tracklog.service.TrackLogService;
import com.woting.crawler.CrawlerConstants;

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
     * 保存数据
     * @param dictMid 字典组id
     * @param parentId 上级结点Id，若为"-1"则在根加入
     * @param Owner 源数据的所有者，如"喜马拉雅"，"荔枝"等
     * @param saveData 被保存的数据(源数据)，其中的数据要和DictDetail中的对上（但如下字段系统自动生成Id，Py等
     * @return -1没有处理
     */
    public int saveDict(String dictMid, String parentId, Owner o, Map<String, Object> saveData) {
        try {
            String nodeName=saveData.get("nodeName")+"";
            if (StringUtils.isNullOrEmptyOrSpace(nodeName)) return -1;
            String ownerKey=o.getKey();
            String bizKey=dictMid+"::"+parentId+"::"+nodeName;
            String dataMd5=DigestUtils.md5Hex(parentId+nodeName);

            DictDetail dd=null;
            synchronized (StoreDictService.lock) {
                //看是否已经有了
                boolean find=false;
                DictModel dictM=_cd.getDictModelById(dictMid);
                TreeNode<? extends TreeNodeBean> tn=dictM.dictTree.findNode(parentId);
                if (tn!=null&&tn.getChildCount()>0) {
                    for (TreeNode<? extends TreeNodeBean> ctn: tn.getChildren()) {
                        if (ctn!=null&&ctn.getNodeName().equals(nodeName)) {
                            find=true;
                            break;
                        }
                    }
                }
                TrackLog tlog=null;
                boolean needWriteLog=false;
                if (find) {//如果找到了，不进行添加，看是否需要记录
                    //看log中是否有对应的记录，若没有加入一条，若有看是否是同一个所属源来的
                    Map<String, Object> dataLogMap=ownerDataLogMap.get(ownerKey);
                    if (dataLogMap==null) {
                        dataLogMap=new HashMap<String, Object>();
                        ownerDataLogMap.put(ownerKey, dataLogMap);
                    }
                    Object log=dataLogMap.get(bizKey);
                    if (log==null) {//从数据库中再取一次
                        tlog=trackLogService.getTLogByBizKey("plat_DictD", o, dataMd5);
                        //多长时间后若没有记录则记录一条
                    }
                } else {//如果没找到，就要进行插入
                    String id=DigestUtils.md5Hex(parentId+nodeName);
                    //构造新的TreeNode对象
                    dd=new DictDetail();
                    dd.setId(id);
                    dd.setParentId(parentId);
                    dd.setDdName(nodeName);
                    //插入树
                    TreeNode<DictDetail> ddNode=new TreeNode<DictDetail>(dd);
                    tn.addChild(ddNode);
                    //插入OperLog表
                }
            }
            //在同步块外再写入数据库，这样快
            //插入数据库
            if (dd!=null) {
                dictService.addDictDetail(dd);
            }
        } catch(Exception e) {
        }
        return 1;
    }
}