package com.example.activitytest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class sendMassage {

    private static final OkHttpClient client = new OkHttpClient();

//    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //设置超时
    //--

    public static String doPostHttpRequest(String url, String json)
            throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder().url(url).post(body)
                .addHeader("content-type", "application/json").build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}
