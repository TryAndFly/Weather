package com.example.weather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weather.R;
import com.example.weather.util.HttpCallbackListener;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.Utility;

/**
 * Created by Administrator on 2016/7/19.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{

    private LinearLayout weatherInfoLayout;//
    private TextView cityNameText;//用于显示城市名
    private TextView publishTimeText;//用于显示发布时间
    private TextView weatherDespText;//用于显示天气描述信息
    private TextView temp1;//显示温度1
    private TextView temp2;//显示温度2
    private TextView currentDateText;//显示当前日期
    private Button switchCityBtn;
    private Button refreshWeatherBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        //初始化各个控件
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText =(TextView)findViewById(R.id.city_name);
        publishTimeText =(TextView)findViewById(R.id.publish_text);
        weatherDespText =(TextView)findViewById(R.id.weather_desp);
        temp1 =(TextView)findViewById(R.id.temp1);
        temp2 =(TextView)findViewById(R.id.temp2);
        currentDateText =(TextView)findViewById(R.id.current_data);
        switchCityBtn = (Button)findViewById(R.id.switch_city);
        refreshWeatherBtn = (Button)findViewById(R.id.refresh_weather);

        switchCityBtn.setOnClickListener(this);
        refreshWeatherBtn.setOnClickListener(this);

        String countryCode = getIntent().getStringExtra("country_code");
        if(!TextUtils.isEmpty(countryCode)){
            //有县级代码时查询天气
            publishTimeText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            //查询天气
            Log.d("test","查询天气");
            queryWeatherCode(countryCode);
        }else {
            //没有县级代码则显示本地天气
            showWeather();
        }
    }

    /**
     * 查询县级代号对应的天气
     */
    private void queryWeatherCode(String countryCode){
        String address = "http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
        //到服务器查询
        Log.d("test","到服务器查询天气");
        queryFromServer(address,"countryCode");
    }

    /**
     * 查询天气代号所对应的天气
     */
    private void queryWeatherInfo(String weatherCode){
        String address = "http://weather.com.cn/data/cityinfo/"+weatherCode+".html";
//        Log.d("test","到服务器查询weatheCode");
        //到服务器查询
        queryFromServer(address,"weatherCode");
    }

    /**
     * 根据传入的地址和类型到服务器查询天气代号或者天气信息
     */
    private void queryFromServer(final String address,final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

                Log.d("test","从服务器返回的数据中解析天气代号"+type);

                if ("countryCode".equals(type)){
                    if (!TextUtils.isEmpty(response)){
                        //从服务器返回的数据中解析出天气代号
                        String [] array = response.split("\\|");
                        if (array != null && array.length == 2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if ("weatherCode".equals(type)){
                    Log.d("test","准备访问"+response);
                    //处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //显示数据
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("test",e.toString());
                        publishTimeText.setText("同步失败");
                    }
                });
            }
        });
    }

    /**
     * 从sharedPrefance中读取数据并显示在界面上
     */
    private void showWeather(){
        Log.d("test","准备显示数据");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(sharedPreferences.getString("city_name",""));
        temp1.setText(sharedPreferences.getString("temp1",""));
        temp2.setText(sharedPreferences.getString("temp2",""));
        weatherDespText.setText(sharedPreferences.getString("weather_desp",""));
        publishTimeText.setText("今天"+sharedPreferences.getString("publish_time","")+"发布");
        currentDateText.setText(sharedPreferences.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(WeatherActivity.this,ChooseAreaActivity.class);
                intent.putExtra("form_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishTimeText.setText("同步中...");
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = sharedPreferences.getString("weather_code","");
                if (!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}
