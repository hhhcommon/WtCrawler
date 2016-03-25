package com.woting.crawler.core.wtcm.service;

import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy(true)
@Service
public class StoreAssetService {

    public void saveAsset(Map<String, Object> oneData) {
        //转换为Asset对象
        //一、保存对象
        //检查看是否已经存储过了，lock1
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
    }

    public void saveSequ(Map<String, Object> oneData) {
    }

}
