package com.example.yanchunguo.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yanchunguo.coolweather.WeatherActivity;
import com.example.yanchunguo.coolweather.gson.Weather;
import com.example.yanchunguo.coolweather.util.HttpUtil;
import com.example.yanchunguo.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager mgr = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;//8小时毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        mgr.cancel(pi);
        mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */

    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = prefs.getString("weather",null);
        // final String weatherId;
        if(weatherStr != null) {
            //有缓存时，直接解析天气数据
            Weather weaher = Utility.handleWeatherRes(weatherStr);
            String weatherId = weaher.basic.weatherId;

            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=3dc6a5a7640c4088b48ae46fa3137397";
            HttpUtil.sendOkHttpReq(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String resTxt = response.body().string();
                    final Weather weather = Utility.handleWeatherRes(resTxt);
                    if(weather != null &&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",resTxt);
                        editor.apply();

                    }
                }
            });
        }

    }
    /**
     * 更新每日一图
     */
    private void updateBingPic(){
        String req = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpReq(req, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();

            }
        });
    }
}