package com.woting.cm.core.dict.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.core.model.tree.TreeNodeBean;
import com.spiritdata.framework.util.TreeUtils;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictMaster;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.model.Owner;
import com.woting.cm.core.dict.persis.po.DictDetailPo;
import com.woting.cm.core.dict.persis.po.DictMasterPo;
import com.woting.exceptionC.Wtcm1000CException;

public class DictService {
    @Resource(name="defaultDAO_CM")
    private MybatisDAO<DictMasterPo> dictMDao;
    @Resource(name="defaultDAO_CM")
    private MybatisDAO<DictDetailPo> dictDDao;

    @PostConstruct
    public void initParam() {
        dictMDao.setNamespace("dMaster");
        dictDDao.setNamespace("dDetail");
    }

    //一、以下是字典相关的操作
    /**
     * 加载字典信息
     */

    public _CacheDictionary loadCache() {
        Owner o=new Owner();
        o.setOwnerType(0);
        o.setOwnerId("0");
        _CacheDictionary _cd = new _CacheDictionary(o);

        try {
            //字典组列表
            Map<String, Object> param=new HashMap<String, Object>();
            param.put("ownerType", "1");
            param.put("sortByClause", "id");
            List<DictMasterPo> _l = dictMDao.queryForList(param);
            if (_l==null||_l.size()==0) return null;
            List<DictMaster> ret = new ArrayList<DictMaster>();
            for (DictMasterPo dmp: _l) {
                DictMaster dm = new DictMaster();
                dm.buildFromPo(dmp);
                ret.add(dm);
            }
            _cd.dmList =(ret.size()==0?null:ret);

            //组装dictModelMap
            if (_cd.dmList!=null&&_cd.dmList.size()>0) {
                //Map主对应关系
                for (DictMaster dm: _cd.dmList) {
                    _cd.dictModelMap.put(dm.getId(), new DictModel(dm));
                }

                //构造单独的字典树
                String tempDmId = "";
                List<DictDetail> templ = new ArrayList<DictDetail>();
                param.put("ownerId", "0");
                param.put("ownerType", "0");
                List<DictDetailPo> _l2 = dictDDao.queryForList("getListByOnwer", param);
                if (_l2==null||_l2.size()==0) return null;
                List<DictDetail> ret2 = new ArrayList<DictDetail>();
                for (DictDetailPo ddp: _l2) {
                    DictDetail dd = new DictDetail();
                    dd.buildFromPo(ddp);
                    ret2.add(dd);
                }

                _cd.ddList=ret2.size()==0?null:ret2;//字典项列表，按照层级结果，按照排序的广度遍历树
                if (_cd.ddList!=null&&_cd.ddList.size()>0) {
                    for (DictDetail dd: _cd.ddList) {
                        if (tempDmId.equals(dd.getMId())) templ.add(dd);
                        else {
                            buildDictTree(templ, _cd);
                            templ.clear();
                            templ.add(dd);
                            tempDmId=dd.getMId();
                        }
                    }
                    //最后一个记录的后处理
                    buildDictTree(templ, _cd);
                }
            }
            return _cd;
        } catch(Exception e) {
            throw new Wtcm1000CException("加载Session中的字典信息", e);
        }
    }
    /*
     * 以ddList为数据源(同一字典组的所有字典项的列表)，构造所有者字典数据中的dictModelMap中的dictModel对象中的dictTree
     * @param ddList 同一字典组的所有字典项的列表
     * @param od 所有者字典数据
     */
    private void buildDictTree(List<DictDetail> ddList, _CacheDictionary cd) {
        if (ddList.size()>0) {//组成树
            DictModel dModel = cd.dictModelMap.get(ddList.get(0).getMId());
            if (dModel!=null) {
                DictDetail _t = new DictDetail();
                _t.setId(dModel.getId());
                _t.setMId(dModel.getId());
                _t.setNodeName(dModel.getDmName());
                _t.setIsValidate(1);
                _t.setParentId(null);
                _t.setOrder(1);
                _t.setBCode("root");
                TreeNode<? extends TreeNodeBean> root = new TreeNode<DictDetail>(_t);
                Map<String, Object> m = TreeUtils.convertFromList(ddList);
                root.setChildren((List<TreeNode<? extends TreeNodeBean>>)m.get("forest"));
                dModel.dictTree = (TreeNode<DictDetail>)root;
                //暂不处理错误记录
            }
        }
    }
}