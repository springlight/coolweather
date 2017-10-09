package com.example.yanchunguo.coolweather.gson;

/**
 * Created by yanchun.guo on 2017/10/9.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
