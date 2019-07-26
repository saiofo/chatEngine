package com.example.activitytest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{

    static String tmp=null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.baidu_item:
                Toast.makeText(this,"百度一下 !",Toast.LENGTH_SHORT).show();
                if (tmp!=null){
                    System.out.println("https://sp0.baidu.com/5a1Fazu8AA54nxGko9WTAnF6hhy/su?wd=${"+tmp+"}&cb=SR");
                    getRequest("https://sp0.baidu.com/5a1Fazu8AA54nxGko9WTAnF6hhy/su?wd=${"+tmp+"}&cb=SR");
                }
                break;
            case R.id.recognize_item:
                Toast.makeText(this,"叮~",Toast.LENGTH_SHORT).show();
                final String url = "https://aip.baidubce.com/rpc/2.0/kg/v1/cognitive/entity_annotation?access_token=24.6374c591c7a16c9f7a499b96e908b87a.2592000.1566093149.282335-16840394";
                final JSONObject map = new JSONObject();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (tmp==null)
                                map.put("data","刘德华的老婆");
                            else
                                map.put("data",tmp);
                            final String str = sendMassage.doPostHttpRequest(url, map.toString());
                            System.out.println(str);

                            map.remove("data");
                            System.out.println("已清空map");

//                            TextView text = (TextView)findViewById(R.id.outputContent);
//                            text.setText(str);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView text = (TextView)findViewById(R.id.outputContent);
                                    text.setText(str);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();



                break;
            default:
                System.out.println("error");
        }
//        return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = (EditText)findViewById(R.id.editContent);
        final TextView text = (TextView)findViewById(R.id.outputContent);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (editText.hasFocus()){
//                    editText.setText("");
//                }

                if (i== EditorInfo.IME_ACTION_DONE){
                    tmp = editText.getText().toString();
                    System.out.println(tmp);
                    text.setText(tmp);
                }
                return false;
            }
        });

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"yoooo~",Toast.LENGTH_SHORT).show();
                getRequest("https://www.baidu.com");
//                finish();
            }
        });

    }

    private void getRequest(String requestURL){
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(requestURL).build();

            System.out.println(Thread.currentThread());

            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(MainActivity.this,"get请求失败",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"get请求数据成功！",Toast.LENGTH_LONG).show();
                            final TextView text = (TextView)findViewById(R.id.outputContent);
                            text.setText(res);
                        }
                    });
                }
            });
    }
}
