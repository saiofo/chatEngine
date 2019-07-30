package com.chatengine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Msg> msgList = new ArrayList<Msg>();

    private EditText inputText;

    private Button send;

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

    private static String receivedMsg = null;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            Bundle bundle = message.getData();
            receivedMsg = bundle.get("text").toString();
            Toast.makeText(MainActivity.this,"收到"+bundle.get("text").toString()+"啦",Toast.LENGTH_SHORT).show();
        }
    };

    public Handler getmHandler() {
        return mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMsgs(); // 初始化消息数据
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        final TextManager textmanager = new TextManager(this);

        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    fresh();

                    //获取输入文本
                    textmanager.setTextContent(content);
                    textmanager.SemanticRecongize();
                    int timeout = 0;

//                    while (!textmanager.flag&&(timeout<30)){
//                        try {
//                            Thread.sleep(1000);
//                            timeout++;
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }

                    if (timeout<30){
                        Msg msg1 = new Msg(receivedMsg, Msg.TYPE_RECEIVED);
                        msgList.add(msg1);
                        fresh();
                    }
                    else
                        Toast.makeText(MainActivity.this,"请求超时",Toast.LENGTH_SHORT).show();

//                    adapter.notifyItemInserted(msgList.size() - 1);
//                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
//                    inputText.setText("");
                }
            }
        });
    }

    /**
     * 初始化聊天消息
     */
    private void initMsgs() {
        Msg msg1 = new Msg("在么？", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("不在，CNM ", Msg.TYPE_SENT);
        msgList.add(msg2);
        Msg msg3 = new Msg("*龙门粗口* ", Msg.TYPE_RECEIVED);
        msgList.add(msg3);
    }

    private void fresh(){
        // 当有新消息时，刷新ListView中的显示
        adapter.notifyItemInserted(msgList.size() - 1);
        // 将ListView定位到最后一行
        msgRecyclerView.scrollToPosition(msgList.size() - 1);
        // 清空输入框中的内容
        inputText.setText("");
    }
}
