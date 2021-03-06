package com.example.caisiyu.bishe_max;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.DialogPreference;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private DrawerLayout mdrawerLayout;
    private TextView Text_wendu;
    private TextView Text_shidu;
    private TextView Text_fengsu;
    private TextView Text_guangzhao;
    private TextView Text_state_wendu, Text_state_shidu, Text_state_fengsu, Text_state_guangzhao,Shebei_State;
    private Handler handler;
    private Timer timer;
    private Vibrator vibrator;
    private ImageView Dream_zhedie;
//    private int Connect_Flag = 0; //在request消息回调是 做标志位
//    private int Connect_Back_Message = 0;

    private List<Fruit> fruitList = new ArrayList<Fruit>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity__two);
        vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);


        timer=new Timer();      //定义一个定时器
//        on_timer_Stop();      //调用 cancel后 就不能调用schedule语句，否则提示出错，提示如下：
        ui_init();
//        okhttp_Request("http://118.178.59.37:1880/Android_Respond?environment=1");
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
                    mdrawerLayout.closeDrawer(Gravity.START);
                    vibrator.vibrate(100);//震动一次，时长为100
                }
                if (i == 1) {
                    Toast.makeText(MainActivity_Two.this, "这是第二个", Toast.LENGTH_SHORT).show();
                    mdrawerLayout.closeDrawer(Gravity.START);
                    vibrator.vibrate(100);//震动一次，时长为100
                }
                if (i == 2) {
                    Toast toast = Toast.makeText(MainActivity_Two.this, "姓名：\t\t\t蔡思宇\r\n学校：\t\t\t郑州升达经贸管理学院\r\n班级：\t\t\t电子信息工程1班\r\n联系QQ：\t2455861209", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM,10,250);
                    toast.show();
                    mdrawerLayout.closeDrawer(Gravity.START);
                    vibrator.vibrate(100);//震动一次，时长为100
                }
                if (i == 3) {
                    Intent intent = new Intent(MainActivity_Two.this, MainActivity_three_quxian.class);
                    startActivity(intent);
                    mdrawerLayout.closeDrawer(Gravity.START);
                    vibrator.vibrate(100);//震动一次，时长为100
//                    finish();     //在另一个界面 点返回还能返回回来 ，并没有 删除当前界面
                }
                if (i == 4) {
//                    Toast.makeText(MainActivity_Two.this, "这是第si个", Toast.LENGTH_SHORT).show();
                    final EditText editText = new EditText(MainActivity_Two.this);
                    editText.setFocusable(true);
                    new AlertDialog.Builder(MainActivity_Two.this)
                            .setTitle("请输入你的评价:")
                            .setIcon(R.drawable.pingjia)
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (!TextUtils.isEmpty(editText.getText().toString())) {
                                        String str = String.format("http://118.178.59.37:1880/Android_Callback?Message=%s", editText.getText().toString());
                                        okhttp_Request(str);
                                        Toast.makeText(MainActivity_Two.this, "十分感谢你的评价！！！", Toast.LENGTH_SHORT).show();
//                                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + editText.getText().toString());
                                    } else {
                                        Toast.makeText(MainActivity_Two.this, "你没有填写评价内容！！！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("取消", null).show();
                    mdrawerLayout.closeDrawer(Gravity.START);
                    vibrator.vibrate(100);//震动一次，时长为100
                }
                if (i == 5) {
                    new AlertDialog.Builder(MainActivity_Two.this)
                            .setTitle("警告~")
                            .setIcon(R.drawable.tishi)
                            .setMessage("确定要退出程序吗？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    System.exit(0);
                                }
                            })
                            .show();
                    mdrawerLayout.closeDrawer(Gravity.START);
                    vibrator.vibrate(100);//震动一次，时长为100
                }
            }
        });

        handler = new Handler() {
                public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        okhttp_Request("http://118.178.59.37:1880/Android_Respond?environment=1");      //请求数据
                        break;
                    case 1:
                        String  msg_json = msg.obj.toString();//
//                         System.out.println("ConnectionSuccess----------" + msg.obj.toString());
                        Node_red_Data_Process(msg_json);
                        handler.removeMessages(1); //销毁消息
                        break;
                    case 2:
                        String  msg_json1 = msg.obj.toString();//
                        Node_red_Data_Failed_SetUI(msg_json1);
                        handler.removeMessages(1); //销毁消息
                        break;
                    default:
                        break;
                }
            }

        };
    }

    public void Node_red_Data_Failed_SetUI(String msg_json){
//        System.out.println("ConnectionSuccess----------" + msg_json.toString());
        Text_wendu.setText("0");
        Text_shidu.setText("0");
        Text_fengsu.setText("0");
        Text_guangzhao.setText("0");
        Text_state_wendu.setText("错误");
        Text_state_shidu.setText("错误");
        Text_state_fengsu.setText("错误");
        Text_state_guangzhao.setText("错误");
        Shebei_State.setText("离线");

        Text_state_wendu.setTextColor(Color.RED);
        Text_state_shidu.setTextColor(Color.RED);
        Text_state_guangzhao.setTextColor(Color.RED);
        Text_state_fengsu.setTextColor(Color.RED);
    }

    public void Node_red_Data_Process(String msg_json){
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

            Text_state_wendu.setTextColor(Color.WHITE);
            Text_state_shidu.setTextColor(Color.WHITE);
            Text_state_guangzhao.setTextColor(Color.WHITE);
            Text_state_fengsu.setTextColor(Color.WHITE);
//            System.out.println("wendu=" + wendu + "shidu=" + shidu + msg_json);
        } catch (JSONException e) {
            e.printStackTrace();
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
        mdrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Dream_zhedie = (ImageView)findViewById(R.id.imag_zhedie);

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
            case R.id.imag_zhedie:
                mdrawerLayout.openDrawer(Gravity.START);
                vibrator.vibrate(100);//震动一次，时长为100
                break;
            default:
                break;
        }
    }


    public void  okhttp_Request(String url) {
//        final String result = "0";
//        Connect_Flag = 0;
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
                System.out.println("********************************get请求失败**************************************");
//                Connect_Flag = 0;
                Message msg = new Message();
                msg.what = 2;   //收到消息标志位
                msg.obj = "failed";
                handler.sendMessage(msg);    // hander 回传
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
//                    Connect_Flag = 1;

                    ResponseBody body = response.body();
                    //这里的body.string 只能调用一次，第二次会被进程销毁清空。如需要多次使用，可以用变量转存
                    Message msg = new Message();
                    msg.what = 1;   //收到消息标志位
                    msg.obj = body.string();
                    handler.sendMessage(msg);    // hander 回传
                }

            }
        });
    }

    public void on_timer_start(){
        super.onStart();
        timer.schedule(task,500,5000);//等1s前   间隔1s偶
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
        Fruit apple = new Fruit("MQTT", R.drawable.mqtt);
        fruitList.add(apple);
        Fruit banana = new Fruit("baidu语音", R.drawable.jiudian);
        fruitList.add(banana);
        Fruit cai1 = new Fruit("关于作者", R.drawable.zuozhe);
        fruitList.add(cai1);
        Fruit cai2 = new Fruit("显示曲线图", R.drawable.zhexian1);
        fruitList.add(cai2);
        Fruit callback = new Fruit("反馈", R.drawable.fankui);
        fruitList.add(callback);
        Fruit tuichau = new Fruit("退出", R.drawable.exit);
        fruitList.add(tuichau);
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
