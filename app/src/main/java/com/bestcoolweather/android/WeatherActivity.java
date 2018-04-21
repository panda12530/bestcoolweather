package com.bestcoolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ScrollingView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestcoolweather.android.gson.Forecast;
import com.bestcoolweather.android.gson.LifestyleBean;
import com.bestcoolweather.android.gson.Weather;
import com.bestcoolweather.android.service.AutoUpdateService;
import com.bestcoolweather.android.util.HttpUtil;
import com.bestcoolweather.android.util.Utility;
import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends BaseActivity {
    private ScrollView weatherlayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashtText;
    private TextView sportText;
    private TextView drsgText;
    private TextView fluText;
    private TextView travText;
    private TextView uvText;
    private TextView airText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;

    public DrawerLayout drawerLayout;

    @Override
    public int getLayoutId() {
        return R.layout.activity_weather;
    }

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLightStatusBar(true);
        setContentView(R.layout.activity_weather);
        //初始化控件
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_lyout);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        weatherlayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText =(TextView)findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashtText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        drsgText = (TextView)findViewById(R.id.drsg_text);
        fluText = (TextView)findViewById(R.id.flu_text);
        travText = (TextView)findViewById(R.id.trav_text);
        uvText = (TextView)findViewById(R.id.uv_text);
        airText = (TextView)findViewById(R.id.air_text);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);
        String bingPic = preferences.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        if(weatherString!=null){
            //有缓存直接解析天气情况;
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherlayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
    }

    /**
     * 加载必应每日一图
     */
    public void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取图片失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    /**
     * 根据天气Id请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" +
                weatherId+"&key=3dd01e2cb75d40aaa9d8d8d4cb794769";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText =response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    public void showWeatherInfo(Weather weather){
        if(weather!=null && "ok".equals(weather.status)) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            forecastLayout.removeAllViews();
            for (Forecast forecast : weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView dateText = (TextView) view.findViewById(R.id.date_text);
                TextView infoText = (TextView) view.findViewById(R.id.info_text);
                TextView maxText = (TextView) view.findViewById(R.id.max_text);
                TextView minText = (TextView) view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.dayinfo);
                maxText.setText(forecast.max);
                minText.setText(forecast.min);
                forecastLayout.addView(view);
            }
            aqiText.setText("63");
            pm25Text.setText("28");
            for (LifestyleBean lifestyleBean : weather.Lifestyle) {
                if ("comf".equals(lifestyleBean.type)) {
                    String comfort = "舒适度:" + lifestyleBean.txt;
                    comfortText.setText(comfort);
                } else if ("drsg".equals(lifestyleBean.type)) {
                    String dress = "穿衣指数:" + lifestyleBean.txt;
                    drsgText.setText(dress);
                } else if ("flu".equals(lifestyleBean.type)) {
                    String flu = "感冒指数:" + lifestyleBean.txt;
                    fluText.setText(flu);
                } else if ("sport".equals(lifestyleBean.type)) {
                    String sport = "运动指数:" + lifestyleBean.txt;
                    sportText.setText(sport);
                } else if ("trav".equals(lifestyleBean.type)) {
                    String travel = "旅游指数:" + lifestyleBean.txt;
                    travText.setText(travel);
                } else if ("uv".equals(lifestyleBean.type)) {
                    String uv = "紫外线指数:" + lifestyleBean.txt;
                    uvText.setText(uv);
                } else if ("cw".equals(lifestyleBean.type)) {
                    String carWasht = "洗车指数:" + lifestyleBean.txt;
                    carWashtText.setText(carWasht);
                } else if ("air".equals(lifestyleBean.type)) {
                    String air = "空气污染扩散条件指数:" + lifestyleBean.txt;
                    airText.setText(air);
                }
            }
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
            weatherlayout.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_LONG).show();
        }
    }
}
