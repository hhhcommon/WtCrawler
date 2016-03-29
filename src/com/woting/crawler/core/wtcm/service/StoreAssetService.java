package com.woting.crawler.core.wtcm.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.cm.core.media.model.MaSource;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.service.MediaService;
import com.woting.cm.core.perimeter.model.Organize;
import com.woting.crawler.core.wtcm.persis.po.ResOrgAssetRefPo;

@Lazy(true)
@Service
public class StoreAssetService {
    public final static Object malock=new Object();

    @Resource
    EtlLogService etlLogService;
    @Resource
    MediaService mediaService;

    private Organize src; //源系统组织机构
    public void setSrcOrg(Organize src) {
        this.src=src;
    }

    public MediaAsset saveAsset(Map<String, Object> oneData, List<String> dictRefs) {
        if (oneData.get("assetId")!=null) return null;

        //一、保存单体节目
        //生成MediaAsset相关对象
        MediaAsset ma=new MediaAsset();
        MaSource mas=new MaSource();
        String _temp;
        ma.setId(SequenceUUID.getPureUUID()); //注意这里是原始Id
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

        //检查对象是否已经存在
        //得到关联对象
        ResOrgAssetRefPo etlRef=new ResOrgAssetRefPo();
        etlRef.setResTableName("wt_MediaAsset");
        etlRef.setRefType("etl");
        etlRef.setOrgId(src.getId()); //喜马拉雅的Id号
        etlRef.setOrigType("sound");
        etlRef.setOrigId(oneData.get("assetId")+"");
        ResOrgAssetRefPo existRef=null;
        boolean exist=false;
        synchronized (StoreAssetService.malock) {
            existRef=etlLogService.getExistRef(etlRef);
        }
        //生成要存储的对象
        MediaAsset existMa=null;
        if (existRef!=null) {//是旧的，看是否需要更新
            exist=true;
            etlRef=existRef;
            ma=mediaService.getMaInfoById(etlRef.getResId());
        }
        //数据库保存
        synchronized (StoreAssetService.malock) {
            if (!exist) {
                //保存单体节目
                mediaService.saveMa(ma);
                //保存多声音源
                mediaService.saveMas(mas);
                //保存与字典项目的关系
                
                //保存关联关系对象
            }
        }

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