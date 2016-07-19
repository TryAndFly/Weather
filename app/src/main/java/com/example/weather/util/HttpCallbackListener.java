package com.example.weather.util;

/**
 * Created by Administrator on 2016/7/18.
 */
public interface HttpCallbackListener {
    void onFinish(String response );
    void onError(Exception e);
}
