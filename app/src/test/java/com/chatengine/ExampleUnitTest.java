package com.chatengine;

import com.chatengine.messageOrganizer.ContentHandle;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

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
}