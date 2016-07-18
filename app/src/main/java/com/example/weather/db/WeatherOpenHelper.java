package com.example.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/7/18.
 */
public class WeatherOpenHelper extends SQLiteOpenHelper {

    /**
     * Province建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province(" +
            "id integer primary key autoincrement," +
            "province_name text," +
            "province_code text)";

    /**
     * City建表语句
     */
    public static final String CREATE_CITY = "create table City(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer)";
    /**
     * Country建表语句
     */
    public  static final String CREATE_COUNTRY = "create table Country(" +
            "id integer primary key autoincrement," +
            "country_name text," +
            "country_code text," +
            "city_id integer)";
    //建库的构造函数
    public WeatherOpenHelper(Context context,String name, int version) {
        //固定库名
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
