package com.example.weather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.model.City;
import com.example.weather.model.Country;
import com.example.weather.model.Province;
import com.example.weather.model.WeatherDB;
import com.example.weather.util.HttpCallbackListener;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;

    private ProgressDialog progressDialog;
    private TextView textView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private WeatherDB weatherDB;
    private List<String> dataList = new ArrayList();

    /**
     * 省列表
     */
    private List<Province>provinceList ;
    /**
     * 市列表
     */
    private List<City>cityList;
    /**
     * 县列表
     */
    private List<Country>countryList;

    /**
     * 选中的省份
     */
    private Province selectProvince;

    /**
     * 选中的市
     */
    private City selectCity;

    /**
     * 当前级别
     */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView)findViewById(R.id.list_view);
        textView = (TextView)findViewById(R.id.title_text);
        //设置适配器
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        //获得数据库实例
        weatherDB = WeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel == LEVEL_PROVINCE){
                    //加载下一级，市
                    selectProvince = provinceList.get(i);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    //加载下一级，县
                    selectCity = cityList.get(i);
                    queryCountries();
                }
            }
        });
        //初始化加载省级城市
        queryProvinces();
    }

    /**
     * 查询全国省份,优先从数据库去查询，如果没有查到在到服务器上查询
     */
    private void queryProvinces(){
        //加载数据库中已有的数据
        provinceList = weatherDB.loadProvince();
        //测试provinceList的数据,数据全部为台湾，即数据载入时出现问题
//        Log.d("ChooseAreaActivity",provinceList.toString());
        if (provinceList.size()>0){
            //清空数据
            dataList.clear();
            //准备加载查询的数据
            Log.d("ChooseAreaActivity","准备加载查询的数据");
            for (Province province:provinceList){
                //加载数据
                dataList.add(province.getProvinceName());
                Log.d("ChooseAreaActivity","加载的数据为"+province.getProvinceName());
            }
            //刷新数据
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            //从服务器查询数据
            queryFormServer(null,"province");
        }
    }
    /**
     * 查询选中的省份的所有城市，优先从数据库去查询，如果没有在到服务器查询
     */
    private void queryCities(){
        cityList = weatherDB.loadCity(selectProvince.getId());
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFormServer(selectProvince.getProvinceCode(),"city");
        }
    }
    /**
     * 查询选择的市的所有县，优先从数据库去查询，如果没有再到服务器查询
     */
    private void queryCountries(){
        countryList = weatherDB.loadCountry(selectCity.getId());
        if (countryList.size()>0){
            dataList.clear();
            for (Country country:countryList){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectCity.getCityName());
            currentLevel = LEVEL_COUNTRY;
        }else {
            queryFormServer(selectCity.getCityCode(),"country");
        }
    }

    /**
     * 根据传入的代号和类型到服务器上查询省市县数据
     */
    private void queryFormServer(final String code,final String type){
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city"+code+ ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(weatherDB,response);
                }else if ("city".equals(type)){
                    result =Utility.handleCitysResponse(weatherDB,response,selectProvince.getId());
                }else if ("country".equals(type)){
                    result = Utility.handleCountrysResponse(weatherDB,response,selectCity.getId());
                }

                if (result){
                    //通过runOnUiThread()方法回到主线程处理
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                Log.d("ChooseAreaActivity","准备查询Provinces");
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("country".equals(type)){
                                queryCountries();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过RunOnUiThread回到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度的对话框
     */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭对话框
     */
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    /**
     * 捕获back键，根据当前级别判断返回市列表，省列表还是直接退出
     */
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (currentLevel == LEVEL_COUNTRY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }
}
