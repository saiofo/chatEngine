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
    List<String> dict = new ArrayList<String>();

    public void setDict(List<String> dict) {
        this.dict = dict;
    }

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

    //文本处理管理类
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void alpha(){

        syntacticAnalysis();

        System.out.println(join("\n",dict));

//        ArrayList<Words> wordsList = contentHandle.createEntity(dict);
//        Words target = contentHandle.findThatOne(wordsList,"word","天气");
//        Words location = contentHandle.findThatOne(wordsList,"postag","ns");
//        String city = null;
//        if (target!=null){
//            if (target.getWord().equals("天气")){
//                if (location==null)
//                    searchWeather("6",null);
//                if (location!=null){
//                    city = location.getWord();
//                    searchWeather("6",city);
//                }
//            }
//        }
        if (textContent.equals("天气"))
            searchWeather("6",null);
        if (textContent.equals("南京天气"))
            searchWeather("6","南京");
        if (textContent.equals("上海天气"))
            searchWeather("6","上海");
        if (!textContent.equals("天气")&&!textContent.equals("南京天气")&&!textContent.equals("上海天气"))
            semanticRecongize();


//        if (textContent.equals("天气"))
//            searchWeather("6",null);
//        else{
//            semanticRecongize();
//            syntacticAnalysis();
//        }
    }

    //依存句法分析
    public void syntacticAnalysis(){
        final String url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/depparser?charset=UTF-8&" +
                "access_token=24.6374c591c7a16c9f7a499b96e908b87a.2592000.1566093149.282335-16840394";
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

                        final String requestStr = sendMessage.doPostHttpRequest(url, map.toString());

                        System.out.println(requestStr);

                        /*
                        文本处理
                         */
                        String str  =null;
                        List<String> getDict = new ArrayList<String>();
                        getDict = contentHandle.searchDictTraget(requestStr,"items");

                        str = join("\n",getDict);

                        //将结果str发送给主线程
//                        sendToActivity(str);

                        //将字典传入TextManager
                        setDict(getDict);

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
                "access_token=24.6374c591c7a16c9f7a499b96e908b87a.2592000.1566093149.282335-16840394";
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
                "access_token=24.6374c591c7a16c9f7a499b96e908b87a.2592000.1566093149.282335-16840394";
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
                        List<String> updateTime = contentHandle.searchTraget(requestWeather,"update_time");
                        String getTime = updateTime.get(0);

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
