package com.chatengine;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Handler;

public class TextManager {
    private String textContent = null;

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getTextContent() {
        return textContent;
    }

    public void SemanticRecongize(final Handler handler){
        final String url = "https://aip.baidubce.com/rpc/2.0/kg/v1/cognitive/entity_annotation?access_token=24.6374c591c7a16c9f7a499b96e908b87a.2592000.1566093149.282335-16840394";
        final JSONObject map = new JSONObject();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (textContent!=null) {
                    try {
                        map.put("data",textContent);
                        final String str = SendMessage.doPostHttpRequest(url, map.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}
