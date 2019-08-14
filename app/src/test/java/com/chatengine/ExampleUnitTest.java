package com.chatengine;

import com.chatengine.messageOrganizer.ContentHandle;
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

    private ContentHandle contentHandle = new ContentHandle();;

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

    //测试enum
}