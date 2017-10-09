package com.example.yanchunguo.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yanchunguo.coolweather.gson.Forecast;
import com.example.yanchunguo.coolweather.gson.Weather;
import com.example.yanchunguo.coolweather.util.HttpUtil;
import com.example.yanchunguo.coolweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;

    private String weatherId;
    private Button btnNav;
    private ScrollView weatherLayout;
    private TextView titleCityTxt;
    private TextView titleUpdateTimeTxt;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiTxt;
    private TextView pm25Txt;
    private TextView comfortTxt;
    private TextView carWashTxt;
    private TextView sportTxt;
    private ImageView bingPicImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initElement();
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 初始化各控件
     */
    private void initElement(){
        weatherLayout = (ScrollView)findViewById(R.id.scroll_view_weather_layout);
        titleCityTxt = (TextView)findViewById(R.id.txt_title_city);
        titleUpdateTimeTxt = (TextView)findViewById(R.id.txt_update_time);
        degreeText = (TextView)findViewById(R.id.txt_degree);
        weatherInfoText = (TextView)findViewById(R.id.txt_weather_info);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        aqiTxt = (TextView)findViewById(R.id.txt_aqi);
        pm25Txt = (TextView)findViewById(R.id.txt_pm25);
        comfortTxt = (TextView)findViewById(R.id.txt_comfort);
        carWashTxt = (TextView)findViewById(R.id.txt_car_wash);
        sportTxt = (TextView)findViewById(R.id.txt_sport);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//设置下拉刷新进度条的颜色
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        btnNav = (Button)findViewById(R.id.btn_nav);
        btnNav.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);//打开滑动菜单
            }
        });
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = prefs.getString("weather",null);
       // final String weatherId;
        if(weatherStr != null){
            //有缓存时，直接解析天气数据
            Weather weaher = Utility.handleWeatherRes(weatherStr);
            weatherId = weaher.basic.weatherId;
            showWeatherInfio(weaher);
        }else {
            //无缓存时去服务器查询天气
             weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            reqWeatherInfo(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reqWeatherInfo(weatherId);
            }
        });

        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }

    }
    /**
     * 处理并展示Weather实体中的数据
     */
    private void showWeatherInfio(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature+"度";
        String weatherInfo = weather.now.more.info;
        titleCityTxt.setText(cityName);
        titleUpdateTimeTxt.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dataTxt = (TextView)view.findViewById(R.id.txt_date);
            TextView infoTxt = (TextView)view.findViewById(R.id.txt_info);
            TextView maxTxt = (TextView)view.findViewById(R.id.txt_max);
            TextView minTxt = (TextView)view.findViewById(R.id.txt_min);
            dataTxt.setText(forecast.date);
            infoTxt.setText(forecast.more.info);
            maxTxt.setText(forecast.temperature.max);
            minTxt.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }
        if(weather.aqi != null){
            aqiTxt.setText(weather.aqi.city.aqi);
            pm25Txt.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度: "+ weather.suggestion.comfort.info;
        String carWash = "洗车指数: "+weather.suggestion.carWash.info;
        String sport = "运动建议： "+ weather.suggestion.sport.info;
        comfortTxt.setText(comfort);
        carWashTxt.setText(carWash);
        sportTxt.setText(sport);
        weatherInfoText.setVisibility(View.VISIBLE);

    }
    /**
     * 根据天气id请求城市天气信息
     */
    public void reqWeatherInfo(final  String weatherId){
        this.weatherId = weatherId;
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=3dc6a5a7640c4088b48ae46fa3137397";
        HttpUtil.sendOkHttpReq(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);//隐藏刷新进度条
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String resTxt = response.body().string();
                final Weather weather = Utility.handleWeatherRes(resTxt);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null &&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",resTxt);
                            editor.apply();
                            showWeatherInfio(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
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

    private void loadBingPic(){
        String req = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpReq(req, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

}
