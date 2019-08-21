package com.chatengine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.chatengine.messageOrganizer.TextManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Msg> msgList = new ArrayList<Msg>();

    private EditText inputText;

    private ImageButton speak;

    private boolean isSpeak;

    private Button send;

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

    private String receivedMsg = null;

//    private Handler m2sHandler = textmanager.getM2sHandler();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            Bundle bundle = message.getData();
            receivedMsg = bundle.get("text").toString();
            Msg rMsg = new Msg(receivedMsg, Msg.TYPE_RECEIVED);
            EventBus.getDefault().post(new Msg(receivedMsg, Msg.TYPE_RECEIVED));
            fresh();
//            Toast.makeText(MainActivity.this,"收到"+bundle.get("text").toString()+"啦",Toast.LENGTH_SHORT).show();
        }
    };

    public Handler getmHandler() {
        return mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册EventBus
        EventBus.getDefault().register(this);

        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        speak = (ImageButton) findViewById(R.id.speak);

        initMsgs(); // 初始化消息数据

        //语音输入入口
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSpeak ==false){
                    speak.setBackgroundResource(R.drawable.keyboard);
                    inputText.setText("说点什么~_(:з」∠)_");
                    inputText.setEnabled(false);
                    isSpeak = true;
                }
                else {
                    speak.setBackgroundResource(R.drawable.phone);
                    inputText.setEnabled(true);
                    inputText.setText("");
                    isSpeak = false;
                }
            }
        });

        //输入消息处理
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        final TextManager textmanager = new TextManager(this);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    fresh();
//                    获取输入文本
                    textmanager.setTextContent(content);

                    textmanager.alpha();
                }
            }
        });

//        msgRecyclerView.addOnItemTouchListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(Msg msg){
        msgList.add(msg);
    }

    /**
     * 初始化聊天消息
     */
    private void initMsgs() {
        Msg msg1 = new Msg("在么？", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("你好，我在 ", Msg.TYPE_SENT);
        msgList.add(msg2);
//        Msg msg3 = new Msg("*龙门粗口* ", Msg.TYPE_RECEIVED);
//        msgList.add(msg3);
    }

    private void fresh(){
        // 当有新消息时，刷新ListView中的显示
        adapter.notifyItemInserted(msgList.size() - 1);
        // 将ListView定位到最后一行
        msgRecyclerView.scrollToPosition(msgList.size() - 1);
        // 清空输入框中的内容
        inputText.setText("");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(v.getWindowToken());   //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],    //得到输入框在屏幕中上下左右的位置
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略
        return false;
    }
}
