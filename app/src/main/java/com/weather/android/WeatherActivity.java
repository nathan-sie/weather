package com.weather.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.weather.android.gson.Forecast;
import com.weather.android.gson.Weather;
import com.weather.android.service.AutoUpdateService;
import com.weather.android.util.HttpUtil;
import com.weather.android.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import static com.weather.android.ChooseAreaFragment.autoid;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    // private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;   //体感温度

    private TextView pm25Text;  //空气湿度

    private TextView fengscText;  //风力

    private TextView visionText;  //能见度

    private TextView airText;  //空气状况

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    private String mWeatherId;
    public int time=1,count=1;

    private ImageView image;

    private ImageView imageforcast;
    //语音播报
    private Button btnPlay;


    private static String autoidd;
    public static String speechstr="null";
    public String citytimeid="null";
    public static int flag=0;

    public boolean isFirstRunVoice;//判断是否第一次使用APP
    SharedPreferences.Editor editorvoice;//判断是否第一次使用APP
    public static int flagvoice=0;
    public int startstop=0;




    //private EditText mResultText;
    private SharedPreferences mSharedPreferences;

    FindCity find=new FindCity();

    private NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        SpeechUtility.createUtility(WeatherActivity.this, "appid=5a046888");
        super.onCreate(savedInstanceState);

        //判断第一次进入时，是否选择完城市，否则第二次进入会产生错误
        WelcomeActivity www=new WelcomeActivity();
        //判断第一次进入时，是否选择完城市，否则第二次进入会产生错误
        SharedPreferences firstenter = this.getSharedPreferences("share", MODE_PRIVATE);
        www.isFirstRun = firstenter.getBoolean("isFirstRun", true);
        www.editor = firstenter.edit();
        www.editor.putBoolean("isFirstRun", false);
        www.editor.commit();

        //识别
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.activity_voice_recognize);
        // 定义获取录音的动态权限



        mSharedPreferences = getSharedPreferences("com.example.thirdsdk", Activity.MODE_PRIVATE);




        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        // 初始化各控件
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        //titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        fengscText = (TextView) findViewById(R.id.fengsc_txt);
        visionText = (TextView) findViewById(R.id.vision_txt);
        airText = (TextView) findViewById(R.id.air_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        image = (ImageView) findViewById(R.id.changeimage);
        //imageforcast = (ImageView) findViewById(R.id.forcastchange);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);


            // 无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);


        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        Button button=(Button)findViewById(R.id.nav_shijian);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count==1) {
                    Toast.makeText(WeatherActivity.this, "每一小时更新一次", Toast.LENGTH_SHORT).show();
                    time=1;
                }
                if(count==2) {
                    Toast.makeText(WeatherActivity.this, "每二小时更新一次", Toast.LENGTH_SHORT).show();
                    time=2;
                }
                if(count==3) {
                    Toast.makeText(WeatherActivity.this, "每六小时更新一次", Toast.LENGTH_SHORT).show();
                    time=6;
                }
                if(count==4) {
                    Toast.makeText(WeatherActivity.this, "每十二小时更新一次", Toast.LENGTH_SHORT).show();
                    time=12;
                }
                if(count==6) {
                    Toast.makeText(WeatherActivity.this, "每二十四小时更新一次", Toast.LENGTH_SHORT).show();
                    time=24;
                }
                count++;
                if(count>6)
                    count=1;
            }

        });

        //判断是否首次使用语音助手
        SharedPreferences sharedPreferences1 = this.getSharedPreferences("share1", MODE_PRIVATE);
        isFirstRunVoice = sharedPreferences1.getBoolean("isFirstRunVoice", true);
        editorvoice = sharedPreferences1.edit();
        //语音识别

        //进入网站看详细数据
        Button skipButton=(Button)findViewById(R.id.ent_right);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.heweather.com"));
                startActivity(intent);
            }
        });

        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }


    }

    /**
     * 根据天气id请求城市天气信息。
     */


    public void requestWeather(final String weatherId) {
        //String weatherUrl = "free-api.heweather.net/s6/weather/?location=" + weatherId + "&key=9f864871b4e74a72b14a028b68266c43"
        //String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        //实现数据长期存储，方便再次进入程序，读取上次城市id

        if (autoid!=null) {
            SharedPreferences setting = getSharedPreferences("setting", 0);
            SharedPreferences.Editor editor = setting.edit();
            editor.putString("apiUrl", autoid).commit();
        }
        if(autoid==null) {
            SharedPreferences setting1 = getSharedPreferences("setting", 0);
            autoid = setting1.getString("apiUrl", "");
        }
        String weatherUrl = "https://free-api.heweather.net/s6/weather/?location=" + autoid + "&key=9f864871b4e74a72b14a028b68266c43";
        System.out.println(weatherUrl);
        System.out.println(weatherUrl);
        if (!speechstr.equals("null")) {
            weatherUrl = find.cityname_id(speechstr);
            flag=1;
        }
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }
    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    String cityName;
    String updateTime;
    String degree;
    String weatherInfo;
    String sportmm;
    private void showWeatherInfo(Weather weather) {
        cityName = weather.basic.cityName;
        updateTime = weather.update.updateTime.split(" ")[1];
        degree = weather.now.temperature;
        weatherInfo = weather.now.condtext;
        titleCity.setText(cityName);
        //titleUpdateTime.setText(updateTime);
        degreeText.setText(degree+ "℃");
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        int j=0,k=0;  //判断循环次数
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            if(k==0){
                dateText.setText(forecast.date.substring(5)+"  今天");
            }
            else if (k==1){
                dateText.setText(forecast.date.substring(5)+"  明天");
            }
            else
                dateText.setText(forecast.date);
            infoText.setText(forecast.condday);

            maxText.setText(forecast.temmax);
            minText.setText(forecast.temmin);
            forecastLayout.addView(view);

            //通知栏通知
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "chat";
                String channelName = "聊天消息";
                int importance = NotificationManager.IMPORTANCE_HIGH;


                channelId = "subscribe";
                channelName = "订阅消息";
                importance = NotificationManager.IMPORTANCE_DEFAULT;

            }
            if(j==1&&(forecast.condday.substring(forecast.condday.length()-1,forecast.condday.length()).equals("雨"))) {
                //showNotification(this, 1, "明日有雨，带上雨伞");
                System.out.println("flag");
                System.out.println(flag);
                System.out.println(flag);
                System.out.println(flag);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(this, "chat")
                        .setContentTitle("明日有雨，带上雨伞")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.dayu)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.dayu))
                        .setAutoCancel(true)
                        .build();
                manager.notify(1, notification);
            }
            j++;k++;
        }
        //if (weather.now != null) {
        aqiText.setText(weather.now.flwen);  //体感温度
        pm25Text.setText(weather.now.xhum);   //空气湿度
        fengscText.setText(weather.now.windsc);
        visionText.setText(weather.now.vision);
        // }

        String[] lifestylesug=new String[8];
        for (int i = 0; i < weather.lifestyleList.size(); i++) {
            lifestylesug[i]=weather.lifestyleList.get(i).sugbrf+ "," + weather.lifestyleList.get(i).sugtxt;
            //System.out.println(s.getId()+"  "+s.getTitle()+"  "+s.getAuthor());
        }

        String air ="空气状况：" + lifestylesug[7];
        String comfort = "舒适度：" + lifestylesug[0];
        String carWash = "洗车指数：" + lifestylesug[6];
        String sport = "运动建议：" + lifestylesug[3];
        sportmm=sport;
        airText.setText(air);
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

        if(weatherInfo.equals("晴")){
            image.setImageResource(R.drawable.sun);
        }
        else if (weatherInfo.equals("多云")){
            image.setImageResource(R.drawable.duoyun);
        }
        else if (weatherInfo.equals("阴")){
            image.setImageResource(R.drawable.yin);
        }
        else if(weatherInfo.equals("雨")){
            image.setImageResource(R.drawable.zhongyu);
        }
        else if (weatherInfo.equals("小雨")){
            image.setImageResource(R.drawable.xiaoyu);
        }
        else if (weatherInfo.equals("中雨")){
            image.setImageResource(R.drawable.zhongyu);
        }
        else if (weatherInfo.equals("阵雨")){
            image.setImageResource(R.drawable.zhenyu);
        }
        else if (weatherInfo.equals("雷阵雨")){
            image.setImageResource(R.drawable.leizhenyu);
        }
        else if (weatherInfo.equals("大雨")| weatherInfo.equals("暴雨")){
            image.setImageResource(R.drawable.dayu);
        }
        else if (weatherInfo.equals("雪")){
            image.setImageResource(R.drawable.xue);
        }
        else if (weatherInfo.substring(weatherInfo.length()-1,weatherInfo.length()).equals("雪")){
            image.setImageResource(R.drawable.xue);
        }
        else {
            image.setImageResource(R.drawable.tianqi);
        }
    }


    //语音播报

}
