package com.bestcoolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.bestcoolweather.android.gson.Weather;
import com.bestcoolweather.android.util.HttpUtil;
import com.bestcoolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingpic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;//这是8小时的毫秒数
        long triggerATime = SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi =  PendingIntent.getActivity(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerATime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateWeather(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);
        if(weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            final String weatherId = weather.basic.weatherId;
            String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" +
                    weatherId+"&key=3dd01e2cb75d40aaa9d8d8d4cb794769";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                      String responseText = response.body().string();
                       Weather weather1 = Utility.handleWeatherResponse(responseText);
                       if(weather1!=null && "ok".equals(weather1.status)) {
                           SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                           editor.putString("weather",responseText );
                           editor.apply();
                       }
                }
            });
        }
    }

    public void updateBingpic(){
        String requestBingPic = "http://guo.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }
}
