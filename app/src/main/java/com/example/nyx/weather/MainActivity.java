package com.example.nyx.weather;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import nyx.util.NetUtil;
import nyx.bean.TodayWeather;
//为更新按钮增加单击事件
public class MainActivity extends Activity implements View.OnClickListener{
    private static final int UPDATE_TODAY_WEATHER=1;
    private ImageView mUpdateBtn;

    private ImageView mCitySelect;//添加选择城市的OnClick事件
    //初始化界面控件
    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,temperatureTv,climateTv,windTv,city_name_Tv;
    private ImageView weatherImg,pmImg;
    private ImageView PM25Img,WeatherImg;
    //调用updateTodayWeather方法更新天气
    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg){
            switch(msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //加载布局
        setContentView(R.layout.weather_info);
        mUpdateBtn=(ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        //检测网络是否可用
        if (NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
            Log.d("weather","网络OK");
            Toast.makeText(MainActivity.this,"网络OK!",Toast.LENGTH_LONG).show();
        }else
        {
            Log.d("weather","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
        }
        mCitySelect=(ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        //调用初始化控件方法
        initView();

    }

    //初始化控件内容
    void initView(){
        city_name_Tv=(TextView)findViewById(R.id.title_city_name);
        cityTv=(TextView)findViewById(R.id.city);
        timeTv=(TextView)findViewById(R.id.time);
        humidityTv=(TextView)findViewById(R.id.humidity);
        weekTv=(TextView)findViewById(R.id.week_today);
        pmDataTv=(TextView)findViewById(R.id.pm_data);
        pmQualityTv=(TextView)findViewById(R.id.pm2_5_quality);
        pmImg=(ImageView)findViewById(R.id.pm2_5_img);
        temperatureTv=(TextView)findViewById(R.id.temperature);
        climateTv=(TextView)findViewById(R.id.climate);
        windTv=(TextView)findViewById(R.id.wind);
        weatherImg=(ImageView)findViewById(R.id.weather_img);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        WeatherImg=(ImageView)findViewById(R.id.weather_img);
        PM25Img=(ImageView)findViewById(R.id.pm2_5_img);
    }
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.title_city_manager){
            Intent i=new Intent(this,SelectCity.class);
            startActivityForResult(i,1);
        }
        if (view.getId() == R.id.title_update_btn) {
            //读取城市
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("weather", "cityCode");
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("weather", "网络OK");
                queryWeatherCode("101010100");//调用queryWeatherCode方法
            } else {
                Log.d("weather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }

        }
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){//接收返回的数据
        if(requestCode==1&&resultCode==RESULT_OK){
            String newCityCode=data.getStringExtra("cityCode");
            Log.d("weather","选择的城市代码为"+newCityCode);
            if (NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("weather","网络OK");
                queryWeatherCode(newCityCode);
            }else{
                Log.d("weather","网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //用updateTodayWeather函数更新UI中的控件
    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度:"+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力"+todayWeather.getFengli());
       if(todayWeather.getPm25()!=null) {
            int pm25 = Integer.parseInt(todayWeather.getPm25());
            if (pm25 <= 50) {
                PM25Img.setImageResource(R.drawable.biz_plugin_weather_0_50);
            } else if (pm25 >= 51 && pm25 <= 100) {
                PM25Img.setImageResource(R.drawable.biz_plugin_weather_51_100);
            } else if (pm25 >= 101 && pm25 <= 150) {
                PM25Img.setImageResource(R.drawable.biz_plugin_weather_101_150);
            } else if (pm25 >= 151 && pm25 <= 200) {
                PM25Img.setImageResource(R.drawable.biz_plugin_weather_151_200);
            } else if (pm25 >= 201 && pm25 <= 300) {
                PM25Img.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }
        }
        if(todayWeather.getType()!=null) {
            Log.d("type", todayWeather.getType());
            switch (todayWeather.getType()) {
                case "晴":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                    case "阴":
                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                        break;
                        case "雾":
                            weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                            break;
                            case "多云":
                                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                                break;
                                case "小雨":
                                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                                    break;
                                    case "中雨":
                                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                                        break;
                                        case "大雨":
                                            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                                            break;
                                            case "阵雨":
                                                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                                                break;
                                                case "雷阵雨":
                                                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                                                    break;
                                                    case "雷阵雨加暴":
                                                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                                                        break;
                                                        case "暴雨":
                                                            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                                                            break;
                                                            case "大暴雨":
                                                                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                                                                break;
                                                                case "特大暴雨":
                                                                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                                                                    break;
                                                                    case "阵雪":
                                                                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                                                                        break;
                                                                        case "暴雪":
                                                                            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                                                                            break;
                                                                            case "大雪":
                                                                                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                                                                                break;
                                                                                case "小雪":
                                                                                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                                                                                    break;
                                                                                    case "雨夹雪":
                                                                                        weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                                                                                        break;
                                                                                        case "中雪":
                                                                                            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                                                                                            break;
                                                                                            case "沙尘暴":
                                                                                                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                                                                                                break;
                                                                                                default:
                                                                                                    break;
            }
        }



        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }
    //获取网络数据
    private void queryWeatherCode(String cityCode){
        final String address="http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("weather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                TodayWeather todayWeather=null;
                try{
                    URL url=new URL(address);//定义url地址
                    con=(HttpURLConnection)url.openConnection();//打开连接
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);//设置连接超时
                    con.setReadTimeout(8000);//设置读取超时
                    InputStream in=con.getInputStream();//得到网络返回的输入流
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String str;
                    while((str=reader.readLine())!=null){
                        response.append(str);
                        Log.d("weather",str);
                    }
                    String responseStr=response.toString();
                    Log.d("weather",responseStr);
                    todayWeather=parseXML(responseStr);//调用解析函数，返回TodayWeather对象
                    if (todayWeather!=null){
                        Log.d("weather",todayWeather.toString());
                        Message msg=new Message();
                        msg.what=UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mHandler.sendMessage(msg);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    if (con!=null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }


//解析函数方法
    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather=null;
        int fengxiangCount=0;
        int fengliCount=0;
        int dateCount=0;
        int highCount=0;
        int lowCount=0;
        int typeCount=0;
        try{
            XmlPullParserFactory fac=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType=xmlPullParser.getEventType();
            Log.d("weather","parseXML");
            while(eventType!=XmlPullParser.END_DOCUMENT){
                switch (eventType){//判断当前时间是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                    break;//判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                     if (xmlPullParser.getName().equals("resp")){
                         todayWeather=new TodayWeather();
                     }
                     if(todayWeather!=null) {

                         if (xmlPullParser.getName().equals("city")) {
                             eventType = xmlPullParser.next();
                             todayWeather.setCity(xmlPullParser.getText());
                         } else if (xmlPullParser.getName().equals("updatetime")) {
                             eventType = xmlPullParser.next();
                             todayWeather.setUpdatetime(xmlPullParser.getText());
                         } else if (xmlPullParser.getName().equals("shidu")) {
                             eventType = xmlPullParser.next();
                             todayWeather.setShidu(xmlPullParser.getText());
                         } else if (xmlPullParser.getName().equals("wendu")) {
                             eventType = xmlPullParser.next();
                             todayWeather.setWendu(xmlPullParser.getText());
                         } else if (xmlPullParser.getName().equals("pm25")) {
                             eventType = xmlPullParser.next();
                             todayWeather.setPm25(xmlPullParser.getText());
                         } else if (xmlPullParser.getName().equals("quality")) {
                             eventType = xmlPullParser.next();
                             todayWeather.setQuality(xmlPullParser.getText());
                         } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                             eventType = xmlPullParser.next();
                             todayWeather.setFengxiang(xmlPullParser.getText());
                             fengxiangCount++;
                         } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                             eventType = xmlPullParser.next();
                             todayWeather.setFengli(xmlPullParser.getText());
                             fengliCount++;
                         } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                             eventType = xmlPullParser.next();
                             todayWeather.setDate(xmlPullParser.getText());
                             dateCount++;
                         } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                             eventType = xmlPullParser.next();
                             todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                             highCount++;
                         } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                             eventType = xmlPullParser.next();
                             todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                             lowCount++;
                         } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                             eventType = xmlPullParser.next();
                             todayWeather.setType(xmlPullParser.getText());
                             typeCount++;
                         }
                     }
                        break;
                     //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                eventType=xmlPullParser.next();
            }
        }catch(XmlPullParserException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return todayWeather;
    }
}
