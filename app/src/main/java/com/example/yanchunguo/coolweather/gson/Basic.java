package com.example.yanchunguo.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yanchun.guo on 2017/10/9.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public  Update update;

    public class  Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
