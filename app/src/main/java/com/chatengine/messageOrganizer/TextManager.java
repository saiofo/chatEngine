package com.chatengine.messageOrganizer;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;

import org.json.JSONObject;

import android.os.Handler;

import androidx.annotation.RequiresApi;

import com.chatengine.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class TextManager {

    //方法调用标记
    public boolean flag = false;

    //输入框输入的文本内容
    private String textContent = null;

    //textManager类的handler
    private Handler sHandler = null;

    //android主线程
    private MainActivity mainActivity = null;

    //返回结果内容处理类
    private ContentHandle contentHandle = new ContentHandle();

    //请求发送类
    SendMessage sendMessage = new SendMessage();

    public TextManager(){

    }

    //创建对象时获取Activity的handler
    public TextManager(Activity activity){
        mainActivity = (MainActivity) activity;
        sHandler = mainActivity.getmHandler();
    }

    //向主线程发送消息
    public void sendToActivity(String TextMsg){
        Bundle bundle=new Bundle();
        bundle.putString("text",TextMsg);//bundle中也可以放序列化或包裹化的类对象数据
        Message message=sHandler.obtainMessage();
        message.setData(bundle);
        message.what = 1;
        sHandler.sendMessage(message);
        flag = true;
    }

    //传入输入文本
    public void setTextContent(String textContent) {
        this.textContent = textContent;
        System.out.println(this.textContent);
    }

    public String getTextContent() {
        return textContent;
    }

    public void alpha(){
        if (textContent.equals("天气"))
            SearchWeather("6",null);
        else
            SemanticRecongize();
    }

    //语义识别功能
    public void SemanticRecongize(){
        final String url = "https://aip.baidubce.com/rpc/2.0/kg/v1/cognitive/entity_annotation?access_token=24.6374c591c7a16c9f7a499b96e908b87a.2592000.1566093149.282335-16840394";
        final JSONObject map = new JSONObject();
        flag = false;

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                if (textContent!=null) {
                    try {
                        map.put("data",textContent);
//                        SendMessage sendMessage = new SendMessage();
                        final String requestStr = sendMessage.doPostHttpRequest(url, map.toString());

                        /*
                        文本处理
                         */
                        String str = null;
                        List<String> getWords = new ArrayList<String>();
                        List<String> getDesc = new ArrayList<String>();
                        getWords = contentHandle.searchTraget(requestStr,"mention");
                        getDesc = contentHandle.searchTraget(requestStr,"desc");

                        if (getWords.size()==getDesc.size()){
                            for (int i=0;i<getWords.size();i++){
                                getWords.set(i,getWords.get(i)+":"+getDesc.get(i));
                            }
                            str = String.join("\n",getWords);
                        }
                        else
                            str = "json获取有误";

                        //将结果str发送给主线程
                        sendToActivity(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                    System.out.println("输入内容为空!");
            }
        }).start();
    }

    //查天气
    public void SearchWeather(String type, final String location){
        final String url = "https://www.tianqiapi.com/api/?version=v"+type+"&city="+location;
        flag = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String requestWeather = sendMessage.doGetHttpRequest(url);

                    /*
                    把返回的数据拼接为可读文本
                     */
                    String tmp = null;
                    List<String> city = contentHandle.searchTraget(requestWeather,"city");
                    String getCity = contentHandle.convertUnicode(city.get(0));
                    if (!location.equals(getCity)){
                        tmp = "没有"+location+"的天气信息";
                    }
                    else{
                        List<String> updateTime = contentHandle.searchTraget(requestWeather,"update_time");
                        String getTime = contentHandle.convertUnicode(updateTime.get(0));

                        List<String> wea = contentHandle.searchTraget(requestWeather,"wea");
                        String getWea = contentHandle.convertUnicode(wea.get(0));

                        tmp = getCity+"天气"+"\n"+getWea+"\n"+getTime;
                    }
                    sendToActivity(tmp);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
