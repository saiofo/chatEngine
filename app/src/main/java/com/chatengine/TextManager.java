package com.chatengine;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;

import org.json.JSONObject;

import android.os.Handler;

public class TextManager {
    public boolean flag = false;

    private String textContent = null;

    private Handler sHandler = null;
//
    private MainActivity mainActivity = null;

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

    public void setTextContent(String textContent) {
        this.textContent = textContent;
        System.out.println(this.textContent);
    }

    public String getTextContent() {
        return textContent;
    }

    public void SemanticRecongize(){
        final String url = "https://aip.baidubce.com/rpc/2.0/kg/v1/cognitive/entity_annotation?access_token=24.6374c591c7a16c9f7a499b96e908b87a.2592000.1566093149.282335-16840394";
        final JSONObject map = new JSONObject();
        flag = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (textContent!=null) {
                    try {
                        map.put("data",textContent);
                        SendMessage sendMessage = new SendMessage();
                        final String str = sendMessage.doPostHttpRequest(url, map.toString());

//                        //将str发给main
//                        EventBus.getDefault().post(new Msg(str, Msg.TYPE_RECEIVED));

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
}
