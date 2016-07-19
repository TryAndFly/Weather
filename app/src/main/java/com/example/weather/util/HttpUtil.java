package com.example.weather.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/7/18.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address ,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection=null;
                URL url = null;
                try {
                    url = new URL(address);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setReadTimeout(8000);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    //将输入流转换成成缓冲流
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String string;
                    while ((string = bufferedReader.readLine()) != null){
                        stringBuilder.append(string);
                    }
                    //
//                    Log.d("HttpUtil",stringBuilder.toString());正常获取
                    if (listener!=null){
                        listener.onFinish(stringBuilder.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener!=null){
                        listener.onError(e);
                    }
                }finally {
                    if (httpURLConnection!=null){
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }
}
