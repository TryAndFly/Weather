package com.example.weather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.weather.db.WeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */
public class WeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "weather";

    /**
     * 数据库版本
     */
    public static final int VERSION =1;

    private static WeatherDB weatherDB;
    private SQLiteDatabase sqLiteDatabase;

    /**
     * 将构造方法私有化,禁止外部访问
     */
    private WeatherDB(Context context){
        WeatherOpenHelper weatherOpenHelper = new WeatherOpenHelper(context,DB_NAME,VERSION);
        sqLiteDatabase=weatherOpenHelper.getWritableDatabase();
    }

    /**
     * 获取WeatherDB实例,使用单例模式
     */
    public synchronized static WeatherDB getInstance(Context context){
        if (weatherDB == null){
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    /**
     * 将Province实例存储到数据库
     */
    public void saveProvince(Province province){
        if (province!=null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("province_name",province.getProvinceName());
            contentValues.put("province_code",province.getProvinceCode());
            Log.d("saveProvince","正要添加到表中的数据"+province.getProvinceName());
            sqLiteDatabase.insert("Province",null,contentValues);
        }
    }
    /**
     * 从数据库读取全国所有省份信息
     */
    public List<Province> loadProvince(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = sqLiteDatabase.query("Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                Province province = new Province();//如果放在外面进行声明，此处数据显示会产生错误
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return list;
    }
    /**
     * 将City实例存储到数据库
     */
    public void saveCity(City city){
        if (city!=null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name",city.getCityName());
            contentValues.put("city_code",city.getCityCode());
            contentValues.put("province_id",city.getProvinceId());
            sqLiteDatabase.insert("City",null,contentValues);
        }
    }
    /**
     * 从数据库读取全国所有城市信息
     */
    public List<City> loadCity(int province_id){
        List<City> list = new ArrayList<City>();
        Cursor cursor = sqLiteDatabase.query("City",null,"province_id = ?",new String[]{String.valueOf(province_id)},null,null,null);

        if (cursor.moveToFirst()){
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(province_id);
                list.add(city);
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return list;
    }


    /**
     * 将Country实例存储到数据库
     */
    public void saveCountry(Country country){
        if (country!=null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("country_name",country.getCountryName());
            contentValues.put("country_code",country.getCountryCode());
            contentValues.put("city_id",country.getCityId());
            sqLiteDatabase.insert("Country",null,contentValues);
        }
    }
    /**
     * 从数据库读取全国所有城市信息
     */
    public List<Country> loadCountry(int city_id){
        List<Country> list = new ArrayList<Country>();
        Cursor cursor = sqLiteDatabase.query("Country",null,"city_id = ?",new String[]{String.valueOf(city_id)},null,null,null);

        if (cursor.moveToFirst()){
            do {
                Country country = new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
                country.setCityId(city_id);
                list.add(country);
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return list;
    }
}
