package com.chatengine.messageOrganizer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentHandle {

    /**
     * 查找返回的json中的特定值
     * @param str 待查找string类型的json数据
     * @param target json中key的名称
     * @return key对应value的list列表
     */
    public List<String> searchTraget(String str, String target){
        List<String> strs = new ArrayList<>();
        //匹配双引号的正则表达式
        String pattStr = "(?<=\""+target+"\": \").*?(?=\")";
        //创建Pattern并进行匹配
        Pattern pattern= Pattern.compile(pattStr);
        Matcher matcher=pattern.matcher(str);
        //将所有匹配的结果打印输出
        while(matcher.find()) {
            System.out.println(matcher.group());
            strs.add(matcher.group());
        }

        return strs;
    }
}
