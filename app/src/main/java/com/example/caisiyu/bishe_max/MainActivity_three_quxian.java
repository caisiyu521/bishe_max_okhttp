package com.example.caisiyu.bishe_max;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity_three_quxian extends ActionBarActivity {
    private LineChart mChart;
    private Date date;
    private Timer timer;
    private Handler handler;

    private String data_wendu = "0";
    private String data_shidu = "0";
    private String data_fengsu = "0";
    private String data_guangzhao = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_three_quxian);
        okhttp_Request("http://118.178.59.37:1880/Android_Respond?environment=1");      //请求数据
        ui_init();              //ui界面的初始化
        chart_init();

        timer=new Timer();      //定义一个定时器
        on_timer_start();


        handler = new Handler() {
            //            @SuppressLint("SetTextI18n")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        okhttp_Request("http://118.178.59.37:1880/Android_Respond?environment=1");      //请求数据

//                        System.out.println("/////////////////////////////////////////////////");
//                        Integer.getInteger(data_shidu);
//                        System.out.println(Integer.parseInt(data_shidu));
//                        Toast.makeText(getApplicationContext(), "helloworld", Toast.LENGTH_SHORT).show();
                        addEntry();         //画一个点
                        break;
                    case 1:
                        String  msg_json = msg.obj.toString();//
                         System.out.println("ConnectionSuccess----------" + msg.obj.toString());
                        Node_red_Data_Process(msg_json);
                        handler.removeMessages(1); //销毁消息
                        break;

                    default:
                        break;
                }
            }
        };

    }

    private void Node_red_Data_Process(String msg_json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(msg_json);
            data_wendu = jsonObject.getString("wendu");
            data_shidu = jsonObject.getString("shidu");
            data_fengsu = jsonObject.getString("fengsu");
            data_guangzhao = jsonObject.getString("guangzhao");
//            System.out.println(data_wendu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void  okhttp_Request(String url) {

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
//                Message msg = new Message();
//                msg.what = 2;   //收到消息标志位
//                msg.obj = "failed";
//                handler.sendMessage(msg);    // hander 回传
            }
            //异步请求(非主线程)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = request.hashCode();
                if (code == HttpURLConnection.HTTP_OK) {
//                    System.out.println("Connection" + code);
                }
                if (response.code() == 200) {
                    System.out.println("接受成功");
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

    private void chart_init() {
        mChart.setDescription("这是一个动态更新的折线图");
        mChart.setNoDataTextDescription("暂时无数据!");
        mChart.setTouchEnabled(true);       //设置支持触控手势
        mChart.setDragEnabled(true);        //可推动
        mChart.setScaleEnabled(true);       //设置推动
        mChart.setDrawGridBackground(false);//后台绘制
        mChart.setPinchZoom(true);          //如果禁用,扩展可以在x轴和y轴分别完成
        mChart.setBackgroundColor(255); //设置图表的背景颜色        浅灰色

        LineData lineData = new LineData();         //创建一个数据源
        lineData.setValueTextColor(Color.WHITE);    //设置数据文本的字体颜色
        mChart.setData(lineData);

        Legend legend = mChart.getLegend();//图表的注解 只有在有数据时才出现
        legend.setEnabled(true);
        legend.setTextSize(10f);
        legend.setForm(Legend.LegendForm.LINE);//设置 标注是线性的
        legend.setTextColor(Color.WHITE);//设置标注的颜色

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);          //x周的坐标线
        xAxis.setAvoidFirstLastClipping(true);                      //////////////////////////////
        xAxis.setSpaceBetweenLabels(5);   //几个列才绘制               //////////////////////////////
        xAxis.setEnabled(true);
        xAxis.setTextSize(10f);          //设置坐标栏字体的大小
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);// 将X坐标轴放置在底部，默认是在顶部。

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaxValue(90f);
        leftAxis.setAxisMinValue(40f);
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisMaxValue(100);
        leftAxis.setAxisMinValue(0);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void addEntry() {           // 添加进去一个坐标点
        LineData data = mChart.getData();//LineData是表里面的数据
        LineDataSet set = data.getDataSetByIndex(0);    //LineDataSet//新建数据源_ 图表标题是
        LineDataSet set1 = data.getDataSetByIndex(1);    //LineDataSet//新建数据源_ 图表标题是
        LineDataSet set2 = data.getDataSetByIndex(2);    //LineDataSet//新建数据源_ 图表标题是
        LineDataSet set3 = data.getDataSetByIndex(3);    //LineDataSet//新建数据源_ 图表标题是

        if(set == null){
            set = createLineDaftest(1);
            data.addDataSet(set);
        }
        if(set1 == null){
            set1 = createLineDaftest(2);
            data.addDataSet(set1);
        }
        if(set2 == null){
            set2 = createLineDaftest(3);
            data.addDataSet(set2);
        }
        if(set3 == null){
            set3 = createLineDaftest(4);
            data.addDataSet(set3);
        }

        date = new Date();  //获得时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");//时间格式化
        String str = simpleDateFormat.format(date);     //获得格式化的时间
//        System.out.println("当前的时间是" + str.toString());
        data.addXValue(str.toString()+"");

//        float f = (float)((Math.random())*20 +50);      //y轴的数据
        Entry entry = new Entry(Integer.parseInt(data_wendu),set.getEntryCount()); //set.getEntryCount()获得的是所有统计图表上的数据点总量（位置），
        data.addEntry(entry, 0);                        //将这个点加入到 LineData是表中 ，

//        Integer.getInteger(data_shidu)
//        float f1 = (float)((Math.random())*20 +50);         //y轴的数据
        Entry entry1 = new Entry(Integer.parseInt(data_shidu),set1.getEntryCount());  //set.getEntryCount()获得的是所有统计图表上的数据点总量（位置），
        data.addEntry(entry1, 1);                           //将这个点加入到 LineData是表中 ， 0代表是第0个表

//        float f3 = (float)((Math.random())*20 +50);         //y轴的数据
        Entry entry2 = new Entry(Integer.parseInt(data_fengsu),set2.getEntryCount());  //set.getEntryCount()获得的是所有统计图表上的数据点总量（位置），
        data.addEntry(entry2, 2);                           //将这个点加入到 LineData是表中 ， 0代表是第0个表

//        float f4 = (float)((Math.random())*20 +50);         //y轴的数据
        Entry entry3 = new Entry(Integer.parseInt(data_guangzhao),set3.getEntryCount());  //set.getEntryCount()获得的是所有统计图表上的数据点总量（位置），
        data.addEntry(entry3, 3);                           //将这个点加入到 LineData是表中 ， 0代表是第0个表


        mChart.notifyDataSetChanged();// 像ListView那样的通知数据更新  让数据更新
        mChart.setVisibleXRangeMaximum(5);//统计表中最多能显示5个数据
        mChart.moveViewToX(data.getXValCount() - 5);    //更新数据后就显示前5个数据
    }

    private LineDataSet createLineDaftest(int caisiyu) {//添加一条统计折线 （就是数据  需要存储的地方）
        LineDataSet set;
        if(caisiyu == 1){
            set = new LineDataSet(null,"湿度");
            set.setColor(Color.BLUE);       //修改这个 折线的颜色 和 提示标签都有
        }
        else if(caisiyu == 2){
            set = new LineDataSet(null,"温度");
            set.setColor(Color.BLACK);      //折线的颜色
        }
        else if(caisiyu == 3){
            set = new LineDataSet(null,"风速");
            set.setColor(Color.WHITE);       //修改这个 折线的颜色 和 提示标签都有
        }
        else {
            set = new LineDataSet(null,"光照");
            set.setColor(Color.YELLOW);      //折线的颜色
        }

        set.setCircleColor(Color.WHITE);        //数据点的颜色
        set.setDrawCircleHole(false);           //设置数据点显示实点还是空心的
        set.setLineWidth(3f);                   //线的宽度
        set.setCircleSize(3f);                  //设置数据点的大小
        set.setFillAlpha(128);
        set.setHighLightColor(Color.GREEN);     //设置点击某个数据点时的焦点轴的颜色
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setValueTextColor(Color.WHITE);    //数据点上面的数值的颜色
        set.setValueTextSize(10f);              //数据点上面的数值的大小
        set.setDrawValues(false);                //不让其显示值
        return set;
    }

    private void ui_init() {
        mChart = (LineChart)findViewById(R.id.chart_data);
    }

    public void on_timer_start(){
        super.onStart();
        timer.schedule(task,500,2000);//等1s前   间隔1s偶
    }

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
        }
    };

}
