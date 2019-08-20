package com.chatengine;

import com.chatengine.messageOrganizer.ContentHandle;
import com.chatengine.wordEntity.Words;
import com.chatengine.wordEntity.propertyEnum.Ask;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static final OkHttpClient client = new OkHttpClient();

    private ContentHandle contentHandle = new ContentHandle();

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
     * 查找返回的json中的特定值
     * @param str 待查找string类型的json数据
     * @param target json中key的名称
     * @return key对应value的list列表
     */
    public List<String> searchTraget(String str, String target){
        List<String> strs = new ArrayList<>();
        //匹配双引号的正则表达式
        String pattStr = "(?<=\""+target+"\":\\s?\").*?(?=\")";
        //匹配没有双引号的正则表达式
        String pattStr1 = "(?<=\""+target+"\":\\s?).*?(?=,)";
        //创建Pattern并进行匹配
        Pattern pattern;
        if (target.equals("id")||target.equals("head"))
            pattern = Pattern.compile(pattStr1);
        else
            pattern = Pattern.compile(pattStr);
        Matcher matcher=pattern.matcher(str);
        //将所有匹配的结果打印输出
        while(matcher.find()) {
//            System.out.println(matcher.group());
            strs.add(matcher.group());
        }

        return strs;
    }

    /**
     * 在一句话的词语实体队列中找出符合条件的词语实体
     * @param words 实体队列
     * @param key 关键词
     * @param value 对应值
     * @return 符合条件实体，没有则返回空
     */
    public Words findThatOne(List<Words> words, String key, String value){
        Words tmp = null;

        for (int i=0;i<words.size();i++){
            switch (key){
                case "id":
                    if (words.get(i).getId().equals(value))
                        tmp = words.get(i);
                    break;
                case "word":
                    if (words.get(i).getWord().equals(value))
                        tmp = words.get(i);
                    break;
                case "postag":
                    if (words.get(i).getPostag().equals(value))
                        tmp = words.get(i);
                    break;
                case "head":
                    if (words.get(i).getHead().equals(value))
                        tmp = words.get(i);
                    break;
                case "deprel":
                    if (words.get(i).getDeprel().equals(value))
                        tmp = words.get(i);
                    break;
            }
            if (tmp!=null){
                return tmp;
            }
        }
        return tmp;
    }

    public static String convert(String utfString){
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
        String endStr = utfString.substring(iint+1, utfString.length());
        return sb+""+endStr;
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public  void doGetHttpRequest() throws IOException {
        String url = "https://www.tianqiapi.com/api/?version=v6&cityid=&city=西安";
        //1 构造Request
        Request.Builder builder = new Request.Builder();
        Request request=new Request.Builder().url(url).get()
                .addHeader("content-type", "application/json").build();

//        Request request=new Request.Builder().url(url).get()
//               .build();

        //2 将Request封装为Call，执行Call，同步发送，得到response
        Response response = client.newCall(request).execute();

        String tmp = response.body().string();

        System.out.println(tmp);

        List<String> oriCity = contentHandle.searchTraget(tmp, "city");

        for (Object obj:oriCity){
            System.out.println(obj+": "+convert((String)obj));
        }

//        convert(tmp);
    }

    /**
     * 返回json中的字典list
     */
    @Test
    public void searchDictTraget(){
        String str = "{\"log_id\": 3902223915521359150, \"text\": \"今天什么天气\", \"items\": [{\"postag\": \"t\", \"head\": 3, \"word\": \"今天\", \"id\": 1, \"deprel\": \"TMP\"}, {\"postag\": \"r\", \"head\": 3, \"word\": \"什么\", \"id\": 2, \"deprel\": \"ATT\"}, {\"postag\": \"n\", \"head\": 0, \"word\": \"天气\", \"id\": 3, \"deprel\": \"HED\"}]}";
        String target = "items";

        List<String> strs = new ArrayList<>();
        List<String> dict = new ArrayList<>();

        //匹配dict(不包含[])的正则表达式
        String pattStr = "(?<=\""+target+"\":\\s?\\[).*?(?=\\]\\})";
        //创建Pattern并进行匹配
        Pattern pattern= Pattern.compile(pattStr);
        Matcher matcher=pattern.matcher(str);
        //将所有匹配的结果打印输出
        while(matcher.find()) {
            System.out.println(matcher.group());
            strs.add(matcher.group());
        }

        for (int i=0;i<strs.size();i++){
            String pattList = "\\{.*?\\}";
            //创建Pattern并进行匹配
            Pattern dictPattern = Pattern.compile(pattList);

            Matcher dictMatcher = dictPattern.matcher(strs.get(i));
            //将所有匹配的结果打印输出
            while(dictMatcher.find()) {
//            System.out.println(matcher.group());
                dict.add(dictMatcher.group());
            }
        }

        for (int i=0;i<dict.size();i++){
            System.out.println(dict.get(i));
        }
    }

    @Test
    //测试contenthandle功能
    public void unitedTestUnit(){

        //返回语义实体字典
        String target = "items";
        String str = "{\"log_id\": 1173747072468426606, \"text\": \"今天南京天气怎样\", \"items\": [{\"postag\": \"t\", \"head\": 4, \"word\": \"今天\", \"id\": 1, \"deprel\": \"TMP\"}, {\"postag\": \"ns\", \"head\": 4, \"word\": \"南京\", \"id\": 2, \"deprel\": \"LOC\"}, {\"postag\": \"n\", \"head\": 4, \"word\": \"天气\", \"id\": 3, \"deprel\": \"SBV\"}, {\"postag\": \"r\", \"head\": 0, \"word\": \"怎样\", \"id\": 4, \"deprel\": \"HED\"}]}";

        List<String> dict = new ArrayList<String>();
        dict = searchDictTraget(str,target);

        for (int i=0;i<dict.size();i++){
            System.out.println(dict.get(i));
        }

        //word实体生成
        Words word = null;
        ArrayList<Words> words = new ArrayList<Words>();

        for (int i = 0;i<dict.size();i++){

//            System.out.println(searchTraget(dict.get(i), "id").get(0));
//            System.out.println(searchTraget(dict.get(i), "head").get(0));

            word = new Words();

            word.setId(searchTraget(dict.get(i), "id").get(0));
            word.setWord(searchTraget(dict.get(i), "word").get(0));
            word.setPostag(searchTraget(dict.get(i), "postag").get(0));
            word.setHead(searchTraget(dict.get(i), "head").get(0));
            word.setDeprel(searchTraget(dict.get(i), "deprel").get(0));

            words.add(word);
        }

//        for (int i=0;i<words.size();i++){
//            System.out.println(words.get(i).getId());
//            System.out.println(words.get(i).getWord());
//            System.out.println(words.get(i).getDeprel());
//            System.out.println(words.get(i).getPostag());
//        }

        Words a = contentHandle.findThatOne(words,"word","天气");

        System.out.println(a.getWord());
    }
}