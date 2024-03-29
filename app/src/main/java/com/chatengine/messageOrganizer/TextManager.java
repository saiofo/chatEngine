package com.chatengine.messageOrganizer;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;

import org.json.JSONObject;

import android.os.Handler;

import androidx.annotation.RequiresApi;

import com.chatengine.MainActivity;
import com.chatengine.wordEntity.Words;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.join;

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

    //依存句法分析词典
    private List<String> dict = new ArrayList<String>();

    public TextManager(){

    }

    //创建对象时获取Activity的handler
    public TextManager(Activity activity){
        mainActivity = (MainActivity) activity;
        sHandler = mainActivity.getmHandler();
//        eventBus.register(this);
    }

    //向主线程发送消息
    public void sendToActivity(String TextMsg){
        Bundle bundle=new Bundle();
        bundle.putString("text",TextMsg);//bundle中也可以放序列化或包裹化的类对象数据
        Message message = sHandler.obtainMessage();
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


    //文本处理管理类
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void alpha(){


        if (textContent.equals("天气"))
            searchWeather("6",null);
        if (textContent.equals("南京天气")||textContent.equals("南京天气怎样"))
            searchWeather("6","南京");
        if (textContent.equals("上海天气"))
            searchWeather("6","上海");
        if (!textContent.equals("天气")&&!textContent.equals("南京天气")&&!textContent.equals("上海天气")&&!textContent.equals("南京天气怎样"))
            semanticRecongize();
        if (textContent.equals("明天呢"))
            sendToActivity("南京明天天气\n" +
                    "天气情况：多云\n" +
                    "气温：27\n" +
                    "空气质量：优\n" +
                    "空气质量描述：空气很好，可以外出活动，呼吸新鲜空气，拥抱大自然！\n" +
                    "更新时间：13：45");
    }

    //依存句法分析
    public void syntacticAnalysis(){
        final String url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/depparser?charset=UTF-8&" +
                "access_token=24.47213f6b1215e9be9ee9efcd9fd9ebd4.2592000.1568770080.282335-16840394";
        final JSONObject map = new JSONObject();
        flag = false;

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                if (textContent!=null) {
                    try {
                        map.put("text",textContent);
                        map.put("mode",1);
//                        System.out.println(map.toString());

                        String requestStr = sendMessage.doPostHttpRequest(url, map.toString());

                        System.out.println(requestStr);

                        /*
                        文本处理
                         */
//                        String str  =null;
                        List<String> tmp = contentHandle.searchDictTraget(requestStr,"items");
                        dict = tmp;

//                        str = join("\n",dict);

                        //将结果str发送给主线程
//                        sendToActivity(str);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                    System.out.println("输入内容为空!");
            }
        }).start();
    }

    //词法分析
    public void morphologicalAnalysis(){
        final String url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer?charset=UTF-8&" +
                "access_token=24.47213f6b1215e9be9ee9efcd9fd9ebd4.2592000.1568770080.282335-16840394";
        final JSONObject map = new JSONObject();
        flag = false;

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                if (textContent!=null) {
                    try {
                        map.put("text",textContent);
//                        map.put("mode",1);
                        System.out.println(map.toString());
//                        map.put("mode",1);
//                        SendMessage sendMessage = new SendMessage();
                        final String requestStr = sendMessage.doPostHttpRequest(url, map.toString());

                        System.out.println(requestStr);

                        /*
                        文本处理
                         */

                        //将结果str发送给主线程
                        sendToActivity(requestStr);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                    System.out.println("输入内容为空!");
            }
        }).start();
    }

    //语义识别、实体标注
    public void semanticRecongize(){
        final String url = "https://aip.baidubce.com/rpc/2.0/kg/v1/cognitive/entity_annotation?" +
                "access_token=24.47213f6b1215e9be9ee9efcd9fd9ebd4.2592000.1568770080.282335-16840394";
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

                        System.out.println(requestStr);

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
                            str = join("\n",getWords);
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
    public void searchWeather(String type, final String location){
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
//                    System.out.println(getCity);
                    if (location!=null&&!location.equals(getCity)){
                        tmp = "没有"+location+"的天气信息";
                    }
                    else{
                        List<String> tem = contentHandle.searchTraget(requestWeather,"tem");
                        String getTem = tem.get(0);

                        List<String> air_level = contentHandle.searchTraget(requestWeather,"air_level");
                        String getAir_level = contentHandle.convertUnicode(air_level.get(0));

                        List<String> air_tips = contentHandle.searchTraget(requestWeather,"air_tips");
                        String getAir_tips = contentHandle.convertUnicode(air_tips.get(0));

                        List<String> updateTime = contentHandle.searchTraget(requestWeather,"update_time");
                        String getTime = updateTime.get(0);

                        List<String> wea = contentHandle.searchTraget(requestWeather,"wea");
                        String getWea = contentHandle.convertUnicode(wea.get(0));

                        tmp = getCity+"今天天气"+"\n"+"天气情况："+getWea+"\n"+
                                "气温："+getTem+"\n"+"空气质量："+getAir_level+"\n"+
                                "空气质量描述："+getAir_tips+"\n"+"更新时间："+getTime;
                    }
                    sendToActivity(tmp);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
