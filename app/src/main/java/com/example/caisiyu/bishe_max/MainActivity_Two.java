package com.example.caisiyu.bishe_max;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity_Two extends ActionBarActivity {
    private Button Btn_deng, Btn_huo, Btn_feng, Btn_men;
    private TextView Text_wendu;
    private TextView Text_shidu;
    private TextView Text_fengsu;
    private TextView Text_guangzhao;
    private TextView Text_state_wendu, Text_state_shidu, Text_state_fengsu, Text_state_guangzhao,Shebei_State;
    private Handler handler;
    private Timer timer;
    private int Connect_Flag = 0; //在request消息回调是 做标志位
    private int Connect_Back_Message = 0;

    private List<Fruit> fruitList = new ArrayList<Fruit>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity__two);
        timer=new Timer();      //定义一个定时器
//        on_timer_Stop();      //调用 cancel后 就不能调用schedule语句，否则提示出错，提示如下：
        ui_init();
        okhttp_Request("http://118.178.59.37:1880/Android_Respond?environment=1");
        on_timer_start();

        initFruits(); // 初始化水果数据
        FruitAdapter adapter = new FruitAdapter(MainActivity_Two.this, R.layout.list_item, fruitList);
        ListView listView = (ListView) findViewById(R.id.MyListView);
        listView.setAdapter(adapter);

        //ListView item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Toast.makeText(MainActivity_Two.this, "这是第一个", Toast.LENGTH_SHORT).show();
                }
                if (i == 1) {
                    Toast.makeText(MainActivity_Two.this, "这是第二个", Toast.LENGTH_SHORT).show();
                }
            }
        });

        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
//                        System.out.println("/////////////////////////////////////////////////");
                        okhttp_Request("http://118.178.59.37:1880/Android_Respond?environment=1");      //请求数据
                        break;
                    case 1:
                        String  msg_json = msg.obj.toString();//                        System.out.println("ConnectionSuccess----------" + msg.obj.toString());
                        Node_red_Data_Process(msg_json);
                        handler.removeMessages(1); //销毁消息
                        break;
                    default:
                        break;
                }
            }

        };

    }

    public void Node_red_Data_Process(String msg_json){
        if(Connect_Flag == 1){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(msg_json);
                String wendu = jsonObject.getString("wendu");
                String shidu = jsonObject.getString("shidu");
                String fengsu = jsonObject.getString("fengsu");
                String guangzhao = jsonObject.getString("guangzhao");
                Text_wendu.setText(wendu);
                Text_shidu.setText(shidu);
                Text_fengsu.setText(fengsu);
                Text_guangzhao.setText(guangzhao);
                Text_state_wendu.setText("正常");
                Text_state_shidu.setText("正常");
                Text_state_fengsu.setText("正常");
                Text_state_guangzhao.setText("正常");
                Shebei_State.setText("在线");
//            System.out.println("wendu=" + wendu + "shidu=" + shidu + msg_json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Text_wendu.setText("0");
            Text_shidu.setText("0");
            Text_fengsu.setText("0");
            Text_guangzhao.setText("0");
            Text_state_wendu.setText("错误");
            Text_state_shidu.setText("错误");
            Text_state_fengsu.setText("错误");
            Text_state_guangzhao.setText("错误");
            Shebei_State.setText("离线");
        }
    }

    /*ui初始化  */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void ui_init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);       //将app的标题去掉
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);   //将app的底部 透明
        Btn_deng = (Button) findViewById(R.id.btn_deng); //按键
        Btn_huo = (Button) findViewById(R.id.btn_huo);
        Btn_feng = (Button) findViewById(R.id.btn_feng);
        Btn_men = (Button) findViewById(R.id.btn_men);
        Text_wendu = (TextView) findViewById(R.id.text_wendu);   //数据 文本
        Text_shidu = (TextView) findViewById(R.id.text_shidu);
        Text_fengsu = (TextView) findViewById(R.id.text_fengsu);
        Text_guangzhao = (TextView) findViewById(R.id.text_guangzhao);
        Text_state_wendu = (TextView) findViewById(R.id.text_state_wendu);//自检
        Text_state_shidu = (TextView) findViewById(R.id.text_state_shidu);
        Text_state_fengsu = (TextView) findViewById(R.id.text_state_fensu);
        Text_state_guangzhao = (TextView) findViewById(R.id.text_state_guangzhao);
        TextView user_name = (TextView) findViewById(R.id.User_name);
        Shebei_State = (TextView) findViewById(R.id.Shebei_State);

        Intent intent = getIntent();
        user_name.setText(intent.getStringExtra("User"));
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onClick(View v) {
        //  Activity 直接实现监听接口
        switch (v.getId()) {
            case R.id.btn_deng:
                if(Objects.equals(Btn_deng.getText().toString(), "开启仓库灯")){
                    okhttp_Request("http://118.178.59.37:1880/Android_Respond_anniu?cangkudeng=1");      //请求数据.
                    Btn_deng.setText("关闭仓库灯");
                }
                else if(Objects.equals(Btn_deng.getText().toString(), "关闭仓库灯")){
                    okhttp_Request("http://118.178.59.37:1880/Android_Respond_anniu?cangkudeng=0");      //请求数据.
                    Btn_deng.setText("开启仓库灯");
                }
                break;
            case R.id.btn_huo:
                if(Objects.equals(Btn_huo.getText().toString(), "开启仓库的灭火装置")){
                    okhttp_Request("http://118.178.59.37:1880/Android_Respond_anniu?miehuo=1");      //请求数据.
                    Btn_huo.setText("关闭仓库的灭火装置");
                }
                else if(Objects.equals(Btn_huo.getText().toString(), "关闭仓库的灭火装置")){
                    okhttp_Request("http://118.178.59.37:1880/Android_Respond_anniu?miehuo=0");      //请求数据.
                    Btn_huo.setText("开启仓库的灭火装置");
                }
                break;
            case R.id.btn_feng:
                if(Objects.equals(Btn_feng.getText().toString(), "开启通风器")){
                    okhttp_Request("http://118.178.59.37:1880/Android_Respond_anniu?tongfeng=1");      //请求数据.
                    Btn_feng.setText("关闭通风器");
                }
                else if(Objects.equals(Btn_feng.getText().toString(), "关闭通风器")){
                    okhttp_Request("http://118.178.59.37:1880/Android_Respond_anniu?tongfeng=0");      //请求数据.
                    Btn_feng.setText("开启通风器");
                }
                break;
            case R.id.btn_men:
                if(Objects.equals(Btn_men.getText().toString(), "开启仓库门")){
                    okhttp_Request("http://118.178.59.37:1880/Android_Respond_anniu?men=1");      //请求数据.
                    Btn_men.setText("关闭仓库门");
                }
                else if(Objects.equals(Btn_men.getText().toString(), "关闭仓库门")){
                    okhttp_Request("http://118.178.59.37:1880/Android_Respond_anniu?men=0");      //请求数据.
                    Btn_men.setText("开启仓库门");
                }
                break;

            default:
                break;
        }
    }


    public void  okhttp_Request(String url) {
//        final String result = "0";
        Connect_Flag = 0;
        //创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        final Request request = new Request.Builder()
                .url(url)//请求的url
                .get()//设置请求方式，get()/post()  查看Builder()方法知，在构建时默认设置请求方式为GET
                .build(); //构建一个请求Request对象

        //创建/Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("**********************************************************************");
                Connect_Flag = 0;
            }
            //异步请求(非主线程)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = request.hashCode();
                if (code == HttpURLConnection.HTTP_OK) {
//                    System.out.println("Connection" + code);
                }

                if (response.code() == 200) {
//                    System.out.println("接受成功");
                    Connect_Flag = 1;
                }
                ResponseBody body = response.body();
                    //这里的body.string 只能调用一次，第二次会被进程销毁清空。如需要多次使用，可以用变量转存
                Message msg = new Message();
                msg.what = 1;   //收到消息标志位
                msg.obj = body.string();
                handler.sendMessage(msg);    // hander 回传
            }
        });
    }

    public void on_timer_start(){
        super.onStart();
        timer.schedule(task,1000,1000);//等1s前   间隔1s偶
    }

    public void on_timer_Stop(){
        super.onStop();
        timer.cancel();
    }

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
        }
    };


    private void initFruits() {
        Fruit apple = new Fruit("这是第一个", R.drawable.shutiao);
        fruitList.add(apple);
        Fruit banana = new Fruit("这是第二个", R.drawable.shutiao);
        fruitList.add(banana);
    }


    public class Fruit {
        private String name;
        private int imageId;

        public Fruit(String name, int imageId) {
            this.name = name;
            this.imageId = imageId;
        }
        public String getName() {
            return name;
        }
        public int getImageId() {
            return imageId;
        }
    }
}
