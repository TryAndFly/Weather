package com.example.weather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.weather.model.City;
import com.example.weather.model.Country;
import com.example.weather.model.Province;
import com.example.weather.model.WeatherDB;

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
}
