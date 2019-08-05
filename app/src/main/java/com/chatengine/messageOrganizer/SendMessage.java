package com.chatengine.messageOrganizer;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendMessage {

    private static final OkHttpClient client = new OkHttpClient();
    private static String result = null;

//    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 同步发送post请求
     * @param url 请求目标url
     * @param json post请求体携带的json
     * @return 以string的形式返回json
     * @throws IOException
     */
    public static String doPostHttpRequest(String url, String json) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder().url(url).post(body)
                .addHeader("content-type", "application/json").build();

        Response response = client.newCall(request).execute();  //同步发送

        return response.body().string();
    }

    /**
     * 同步发送get请求
     * @param url 目标url
     * @return String类型返回response
     * @throws IOException
     */
    public  String doGetHtpRequest(String url) throws IOException{
        //1 构造Request
//        Request.Builder builder = new Request.Builder();
        Request request=new Request.Builder().url(url).get()
                .addHeader("content-type", "application/json").build();

        //2 将Request封装为Call，执行Call，同步发送，得到response
        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}