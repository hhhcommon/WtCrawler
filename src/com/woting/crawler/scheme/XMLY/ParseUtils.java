package com.woting.crawler.scheme.XMLY;

import java.util.Map;

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
        if (_s[_s.length-2].equals("zhubo")) return 3;//主播
        if (_s[_s.length-2].equals("tag")) return 4;//标签
        return 0;
    }

    public static Map<String, Object> parseSond(byte[] htmlByteArray) {
        return null;
    }
}