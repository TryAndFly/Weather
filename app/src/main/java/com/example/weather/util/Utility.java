package com.example.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.weather.model.City;
import com.example.weather.model.Country;
import com.example.weather.model.Province;
import com.example.weather.model.WeatherDB;

import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/7/18.
 */
public class Utility  {
    /**
     * 解析和处理服务器返回的省级元素
     */
    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB,String string){
        if (!TextUtils.isEmpty(string)){
            Log.d("Utility","准备解析数据"+string);
            //设置分割符，按各个省份解析
            String [] allProvince = string.split(",");
            Log.d("Utility","解析的第一个数据"+allProvince[0]+"解析的第二个数据"+allProvince[1]);
            if (allProvince != null && allProvince.length>0 ){
                for (String p :allProvince){

                    //将省份数据继续解析后构建省份实例
                    String [] array = p.split("\\|");

                    Province province =new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    Log.d("Utility","准备添加到表中"+province.getProvinceName());
                    //添加到表中
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析处理服务器返回的市数据
     */
    public synchronized static boolean handleCitysResponse(WeatherDB weatherDB,String string,int provinceId){
        if (!TextUtils.isEmpty(string)){
            //设置分割符，按各个省份解析
            String [] allCity = string.split(",");
            if (allCity != null && allCity.length>0 ){
                for (String p :allCity){

                    //将市份数据继续解析后构建实例
                    String [] array = p.split("\\|");

                    City city =new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);

                    //添加到表中
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析处理服务器返回的市数据
     */
    public synchronized static boolean handleCountrysResponse(WeatherDB weatherDB,String string,int cityId){
        if (!TextUtils.isEmpty(string)){
            //设置分割符，按各个省份解析
            String [] allCountry = string.split(",");
            if (allCountry != null && allCountry.length>0 ){
                for (String p :allCountry){

                    //将市份数据继续解析后构建实例
                    String [] array = p.split("\\|");

                    Country country =new Country();
                    country.setCountryCode(array[0]);
                    country.setCountryName(array[1]);
                    country.setCityId(cityId);

                    //添加到表中
                    weatherDB.saveCountry(country);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据,并将解析的数据返回到本地
     */

    public static void handleWeatherResponse(Context context,String response){
        try {
//            Log.d("test","handleWeatherResponse测试");
            //输出数据进行测试
            Log.d("test","返回的数据为:"+response);
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");

            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            //将解析出来的数据保存
            saveWeather(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("test","handleWeatherResponse IO测试2"+e.toString());
        }
    }

    /**
     * 将服务器返回的所有天气信息存储到本地，使用SharedPrefances存储
     */

    public static void saveWeather(Context context,String cityName,String weatherCode,
                                   String temp1,String temp2,String weatherDesp, String publishTime){
        Log.d("test","正在保存数据");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
//        Log.d("test","保存数据");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("weather_time",publishTime);
//        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
        Log.d("test","保存数据结束");
    }
}
