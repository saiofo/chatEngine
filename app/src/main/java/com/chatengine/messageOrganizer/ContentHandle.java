package com.chatengine.messageOrganizer;

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
        String pattStr = "(?<=\""+target+"\":\\s?\").*?(?=\")";
        //创建Pattern并进行匹配
        Pattern pattern= Pattern.compile(pattStr);
        Matcher matcher=pattern.matcher(str);
        //将所有匹配的结果打印输出
        while(matcher.find()) {
//            System.out.println(matcher.group());
            strs.add(matcher.group());
        }

        return strs;
    }

    /**
     * 返回json中的字典list
     * @param str 待查找string类型的json数据
     * @param target json中list的名称
     * @return list对应dict
     */
    public List<String> searchDictTraget(String str, String target){
        List<String> strs = new ArrayList<>();
        List<String> dict = new ArrayList<>();

        //匹配dict(不包含[])的正则表达式
        String pattStr = "(?<=\""+target+"\":\\s?\\[).*?(?=\\]\\})";
        //创建Pattern并进行匹配
        Pattern pattern= Pattern.compile(pattStr);
        Matcher matcher=pattern.matcher(str);
        //将所有匹配的结果输出
        while(matcher.find()) {
            strs.add(matcher.group());
        }

        for (int i=0;i<strs.size();i++){
            String pattList = "\\{.*?\\}";
            //创建Pattern并匹配每一个词
            Pattern dictPattern = Pattern.compile(pattList);
            Matcher dictMatcher = dictPattern.matcher(strs.get(i));
            //将所有匹配的结果输出
            while(dictMatcher.find()) {
//            System.out.println(matcher.group());
                dict.add(dictMatcher.group());
            }
        }

        return dict;
    }

    /**
     * 将汉字字节码转为中文字符
     * @param utfString utf字节码（目前不支持识别字节码以外内容）
     * @return 中文字符串
     */
    public String convertUnicode(String utfString){
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;
        int  iint=0;
        while((i=utfString.indexOf("\\u", pos)) != -1){
            String sd = utfString.substring(pos, i);
            sb.append(sd);
            iint = i+5;

            if(iint < utfString.length()){
                pos = i+6;
                sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
            }
        }
//        String endStr = utfString.substring(iint+1, utfString.length());

//        return sb+""+endStr;
        return  sb.toString();
    }
}
