package com.woting.crawler.core.wtcm.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.model.DictRefRes;
import com.woting.cm.core.dict.service.DictService;
import com.woting.cm.core.media.model.MaSource;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.service.MediaService;
import com.woting.cm.core.perimeter.model.Organize;
import com.woting.crawler.CrawlerConstants;
import com.woting.crawler.core.wtcm.persis.po.ResOrgAssetRefPo;

@Lazy(true)
@Service
public class StoreAssetService {
    public final static Object malock=new Object();

    @Resource
    EtlLogService etlLogService;
    @Resource
    MediaService mediaService;
    @Resource
    DictService dictService;

    private Organize src; //源系统组织机构
    public void setSrcOrg(Organize src) {
        this.src=src;
    }

    private _CacheDictionary _cd=null;
    @PostConstruct
    public void initParam() {
        //初始化字典
        _cd = ((CacheEle<_CacheDictionary>)SystemCache.getCache(CrawlerConstants.CM_DICT)).getContent();
    }
    
    public MediaAsset saveAsset(Map<String, Object> oneData, List<String> dictRefs) {
        if (oneData.get("assetId")!=null) return null;

        //一、保存单体节目
        String assetId=SequenceUUID.getPureUUID();
        //检查对象是否已经存在
        //得到关联对象
        ResOrgAssetRefPo existRef=null;
        ResOrgAssetRefPo etlRef=new ResOrgAssetRefPo();
        etlRef.setResTableName("wt_MediaAsset");
        etlRef.setRefType("etl");
        etlRef.setOrgId(src.getId()); //喜马拉雅的Id号
        etlRef.setOrigType("sound");
        etlRef.setOrigId(oneData.get("assetId")+"");
        synchronized (StoreAssetService.malock) {
            existRef=etlLogService.getExistRef(etlRef);
        }
        //生成MediaAsset相关对象
        MediaAsset ma=new MediaAsset();
        MaSource mas=new MaSource();
        String _temp;
        ma.setId(assetId); //注意这里是原始Id
        ma.setMaTitle(oneData.get("assetName")+"");
        ma.setMaImg(oneData.get("imgUrl")+"");
        ma.setSubjectWords("");
        ma.setMaURL(oneData.get("playUrl")+"");
        if (oneData.get("tags")!=null) {
            _temp=oneData.get("tags")+"";
            if (_temp.endsWith(",")) 
            ma.setKeyWords(_temp.substring(0, _temp.length()-1));
        }
        Map<String, Object> extInfo=(Map<String, Object>)JsonUtils.jsonToObj(oneData.get("extInfo")+"", Map.class);
        ma.setTimeLong(Integer.parseInt(extInfo.get("duration")+"")*1000);
        ma.setDescn(oneData.get("descript")+"");
        //生成对应的声音源
        mas.setId(SequenceUUID.getPureUUID());
        mas.setSrc(src);
        mas.setIsMain(1);
        mas.setMa(ma);
        mas.setPlayURI(oneData.get("playUrl")+"");
        MediaAsset existM=null;
        if (existRef!=null) {
            existM=mediaService.getMaInfoById(etlRef.getResId());
        }
        //比较，看看是否需要修改：
        
        //二、单体节目数据库保存
        synchronized (StoreAssetService.malock) {
            existRef=etlLogService.getExistRef(etlRef);
            if (existRef!=null) {
                mediaService.saveMa(ma);//保存单体节目
                mediaService.saveMas(mas);//保存多声音源
            } else {
                etlRef=existRef;
            }
            etlLogService.save(etlRef);//保存对应关系
            assetId=etlRef.getResId();
        }
        //保存与字典项目的关联关系，注意这可能有重复的，但由于唯一索引的控制，不会出现重复的值被存储
        if (dictRefs!=null) {
            for (String ds: dictRefs) {
                String[] s=ds.split("::");
                if (s.length!=3) continue;
                DictModel dm=_cd.getDictModelById(s[1]);
                if (dm==null) continue;
                TreeNode<DictDetail> tdd=(TreeNode<DictDetail>)dm.dictTree.findNode(s[2]);
                if (tdd==null) continue;

                DictRefRes drr=new DictRefRes();
                drr.setId(SequenceUUID.getPureUUID());
                drr.setRefName("单体-"+dm.getDmName());
                drr.setResTableName("wt_MediaAsset");
                drr.setResId(assetId);
                drr.setDm(dm);
                drr.setDd(tdd.getTnEntity());
                dictService.bindDictRef(drr);
            }
        }
        //保存关联关系对象

        //二、所属专辑处理
        //1-查看缓存
        //2-查看数据库
        //若没有处理过
        //1-插入缓存
        //2-插入数据：包括对象本身和来源关系表
        //二、保存所属专辑
        //a)检查专辑是否有了
        //1、从未处理专辑缓存查
        //2、从数据库查
        //若没有加入未处理专辑缓存
        //b)保存专辑和资源的关系
        //三、保存资源的关联关系，主要是和字典的关系
        //四、最后计入日志表，注意，这里的日志只对主对象，相关的其他不予记录，包括缓存中的数据
        return null;
    }

    public void saveSequ(Map<String, Object> oneData, List<String> dictRefs) {
    }
}