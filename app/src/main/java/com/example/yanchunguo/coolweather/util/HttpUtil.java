package com.example.yanchunguo.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by yanchun.guo on 2017/9/29.
 */

public class HttpUtil {
    public static void sendOkHttpReq(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().url(address).build();
        client.newCall(req).enqueue(callback);
    }
}
