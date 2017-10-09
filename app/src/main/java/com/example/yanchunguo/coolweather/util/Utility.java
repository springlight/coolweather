package com.example.yanchunguo.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.yanchunguo.coolweather.db.City;
import com.example.yanchunguo.coolweather.db.County;
import com.example.yanchunguo.coolweather.db.Province;
import com.example.yanchunguo.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by yanchun.guo on 2017/9/29.
 */

public class Utility {
    private static final String TAG = "Utility";

    /**
     * 解析和处理服务器返回的升级数据
     */
    public static boolean handleProvinceRes(String res){
        if(!TextUtils.isEmpty(res)){
            try{
                JSONArray allProvines = new JSONArray(res);
                for(int i = 0; i < allProvines.length();i++){
                    JSONObject provinceObj = allProvines.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObj.getString("name"));
                    province.setProvinceCode(provinceObj.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityRes(String res,int provinceId){
        if(!TextUtils.isEmpty(res)){
            try{
                JSONArray allCities = new JSONArray(res);
                for(int i = 0; i < allCities.length();i++){
                    JSONObject cityObj = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObj.getString("name"));
                    city.setCityCode(cityObj.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyRes(String res,int cityId){
        if(!TextUtils.isEmpty(res)){
            try{
                JSONArray allCounties = new JSONArray(res);
                for(int i = 0;i < allCounties.length();i++){
                    JSONObject countyObj = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObj.getString("name"));
                    county.setWeatherId(countyObj.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将返回的JSON数据解析成Weather实体类
     */
    public static Weather handleWeatherRes(String res){
        try{
            JSONObject jsonObject = new JSONObject(res);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Log.d(TAG, "handleWeatherRes:weatherContent 11111111111"+weatherContent);
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
