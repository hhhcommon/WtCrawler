package com.woting.crawler.scheme.XMLY.crawl;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.crawler.core.HttpUtils;

public abstract class ParseUtils {
    /**
     * 得到内容类型，喜马拉雅采用Rest风格
     * @param href
     * @return 类型0不符合的类型,1专辑；2声音；3主播；4标签
     */
    public static int getType(String href) {
        String[] _s=href.substring(9).split("/");
        if (_s.length<2) return 0;
        if (_s[_s.length-2].equals("album")) return 1;//专辑
        if (_s[_s.length-2].equals("sound")) return 2;//声音
//        if (_s[_s.length-2].equals("zhubo")) return 3;//主播
//        if (_s[_s.length-2].equals("tag")) return 4;//标签
        return 0;
    }
    /**
    parseData.put("seqId", "");
    parseData.put("assetId", "");
    parseData.put("assetName", "");
    parseData.put("person", "");
    parseData.put("playCount", "");
    parseData.put("descript", "");
    parseData.put("extInfo", "");
     */

    /**
     * 分析专辑
     * @param htmlByteArray html内容
     * @param parseData 返回的数据
     */
    public static void parseAlbum(byte[] htmlByteArray, Map<String, Object> parseData) {
        Elements eles=null;
        Element e=null;
        Map<String, Object> extInfo=new HashMap<String, Object>();
        Document doc=Jsoup.parse(new String(htmlByteArray), "UTF-8");

        //得到名称、ID、img
        try {
            eles=doc.select("div.personal_body").select("div.left").select("img");
            if (eles!=null&&!eles.isEmpty()) {
                e=eles.get(0);
                parseData.put("seqName", e.attr("alt").trim());
                extInfo.put("seqLageImg", e.attr("popsrc").trim());
                parseData.put("imgUrl", e.attr("src").trim());
            }
        } catch(Exception ex) {ex.printStackTrace();}
        //类别
        try {
            eles=doc.select("div.detailContent_category");
            if (eles!=null&&!eles.isEmpty()) {
                e=eles.get(0);
                parseData.put("catalog", e.select("a").get(0).html().trim());
                parseData.put("playUrl", e.select("a").get(0).attr("href").trim());
                String lastUpdateDay=e.select("span").html();
                if (!StringUtils.isNullOrEmptyOrSpace(lastUpdateDay)) {
                    lastUpdateDay=lastUpdateDay.substring(lastUpdateDay.lastIndexOf(":")+1);
                    extInfo.put("lastUpdateTime", lastUpdateDay.trim());
                }
            }
        } catch(Exception ex) {ex.printStackTrace();}
        //标签
        try {
            String tags="";
            eles=doc.select("div.tagBtnList");
            if (eles!=null&&!eles.isEmpty()) {
                eles=eles.select("span");
                for (int i=0; i<eles.size(); i++) {
                    e=eles.get(i);
                    tags+=","+e.select("span").html();
                }
            }
            if (tags.length()>0) parseData.put("tags", tags.substring(1).trim());
        } catch(Exception ex) {ex.printStackTrace();}
        //播放数
        try {
            eles=doc.select("div.detailContent_playcountDetail");
            if (eles!=null&&!eles.isEmpty()) {
                parseData.put("playCount", ParseUtils.getFirstNum(eles.select("span").html()));
            }
        } catch(Exception ex) {ex.printStackTrace();}
        //描述
        try {
            eles=doc.select("div.detailContent_intro");
            if (eles!=null&&!eles.isEmpty()) {
                parseData.put("descript", eles.select("div.mid_intro").select("article").get(0).html().trim());
                extInfo.put("seqDescn", eles.select("div.rich_intro").select("article").get(0).html().trim());
            }
        } catch(Exception ex) {ex.printStackTrace();}
        //专辑
        try {
            //声音数
            eles=doc.select("span.albumSoundcount");
            if (eles!=null&&!eles.isEmpty()) {
                extInfo.put("seqCount", ParseUtils.getFirstNum(eles.html()));
                //eles=doc.select("div.personal_body").select("div.detailContent").select("div.c1").select("div.right").select("a.shareLink shareLink2");
                eles=doc.select("a.shareLink");
                extInfo.put("zhuboId", eles.get(0).attr("album_uid").trim());
                parseData.put("seqId", eles.get(0).attr("album_id").trim());
            }
        } catch(Exception ex) {ex.printStackTrace();}
        //扩展内容
        try {
            if (!extInfo.isEmpty()) {
                parseData.put("extInfo", JsonUtils.objToJson(extInfo));
            }
        } catch(Exception ex) {ex.printStackTrace();}
    }

    /**
     * 分析声音
     * @param htmlByteArray html内容
     * @param parseData 返回的数据
     */
    public static void parseSond(byte[] htmlByteArray, Map<String, Object> parseData) {
        Elements eles=null;
        Element e=null;
        Map<String, Object> extInfo=new HashMap<String, Object>();
        Document doc=Jsoup.parse(new String(htmlByteArray), "UTF-8");

        //得到名称、ID、img
        try {
            eles=doc.select("img[sound_popsrc]");
            if (eles!=null&&!eles.isEmpty()) {
                e=eles.get(0);
                parseData.put("assetId", e.attr("sound_popsrc").trim());
                parseData.put("assetName", e.attr("alt").trim());
                parseData.put("imgUrl", e.attr("src").trim());
            }
        } catch(Exception ex) {ex.printStackTrace();}
        //声音
        try {
            eles=doc.select("div.detail_soundBox2");
            extInfo.put("sound_duration", eles.select("span.sound_duration").first().html());
            //播放URL
            Map<String, Object> m=HttpUtils.getJsonMapFromURL("http://www.ximalaya.com/tracks/"+parseData.get("assetId")+".json");
            if (m!=null) {
                if ((parseData.get("assetName")+"").equals(m.get("title")+"")) {
                    extInfo.putAll(m);
                    parseData.put("playUrl", m.get("play_path"));
                }
            }
        } catch(Exception ex) {ex.printStackTrace();}
        if (parseData.get("playUrl")==null) return;//若获得不到声音，则不用进行后续处理了，没有声音的也入原始库
        //类别
        try {
            eles=doc.select("div.detailContent_category");
            if (eles!=null&&!eles.isEmpty()) {
                e=eles.get(0);
                parseData.put("catalog", e.select("a").get(0).html().trim());
                extInfo.put("seqUrl", e.select("a").get(0).attr("href").trim());
            }
        } catch(Exception ex) {ex.printStackTrace();}
        //标签
        try {
            String tags="";
            eles=doc.select("div.tagBtnList");
            if (eles!=null&&!eles.isEmpty()) {
                eles=eles.select("span");
                for (int i=0; i<eles.size(); i++) {
                    e=eles.get(i);
                    tags+=","+e.select("span").html();
                }
            }
            if (tags.length()>0) parseData.put("tags", tags.substring(1));
        } catch(Exception ex) {ex.printStackTrace();}
        //播放数
        try {
            eles=doc.select("div.soundContent_playcount");
            if (eles!=null&&!eles.isEmpty()) {
                parseData.put("playCount", ParseUtils.getFirstNum(eles.get(0).html()));
            }
        } catch(Exception ex) {ex.printStackTrace();}
        //描述
        try {
            eles=doc.select("div.detailContent_intro");
            if (eles!=null&&!eles.isEmpty()) {
                parseData.put("descript", eles.get(0).select("article").get(0).html().trim());
            }
        } catch(Exception ex) {ex.printStackTrace();}
        //专辑
        try {
            eles=doc.select("div.albumBar");
            if (eles!=null&&!eles.isEmpty()) {
                eles=eles.get(0).select("li");
                if (eles!=null&&!eles.isEmpty()) {
                    e=eles.first();
                    if (e!=null) {
                        e=e.select("div.right").get(0);
                        parseData.put("seqName", e.select("a").get(0).attr("title").trim());
                        String s=e.select("a").get(0).attr("href");
                        if (s.startsWith("/")) s=s.substring(1);
                        String[] _s=s.split("/");
                        if (_s.length==3) {
                            extInfo.put("zhuboId", _s[0].trim());
                            parseData.put("seqId", _s[2].trim());
                            extInfo.put("seqCount", ParseUtils.getFirstNum(e.select("span").first().html()));
                        }
                    }
                }
            }
        } catch(Exception ex) {ex.printStackTrace();}
        //扩展内容
        try {
            if (!extInfo.isEmpty()) {
                parseData.put("extInfo", JsonUtils.objToJson(extInfo));
            }
        } catch(Exception ex) {ex.printStackTrace();}
    }

    /**
     * 分析主播
     * @param htmlByteArray html内容
     * @param parseData 返回的数据
    public static void parseZhubo(byte[] htmlByteArray, Map<String, Object> parseData) {
    }

    /**
     * 分析标签
     * @param htmlByteArray html内容
     * @param parseData 返回的数据
    public static void parseTags(byte[] htmlByteArray, Map<String, Object> parseData) {
    }
    */

    public static long getFirstNum(String str) {
        long ret=0l;
        StringBuffer firstNumStr=new StringBuffer();
        char[] c=str.toCharArray();
        boolean begin=false, end=false, isDig=false, isFirstDot=true;
        int i=0;
        for (; i<c.length; i++) {
            isDig=Character.isDigit(c[i]);
            if (isDig&&!begin) begin=true;
            if (begin) {
                if (isDig) firstNumStr.append(c[i]);
                if (!isDig) {
                    //是.
                    if (c[i]=='.') {
                        if (isFirstDot) {
                            isFirstDot=false;
                            firstNumStr.append(c[i]);
                            continue;
                        }
                    }
                    if (c[i]=='万') firstNumStr.append(c[i]);
                    end=true;
                }
            }
            if (end) break;
        }
        String _firstNumStr=firstNumStr.toString();
        int gene=1;
        if (_firstNumStr.endsWith("万")) {
            gene=10000;
            _firstNumStr=_firstNumStr.substring(0, _firstNumStr.length()-1);
        }
        if (_firstNumStr.endsWith(".")) _firstNumStr+="0";
        float f=Float.parseFloat(_firstNumStr);
        f=f*gene;
        ret=(new Float(f)).longValue();
        return ret;
    }
}