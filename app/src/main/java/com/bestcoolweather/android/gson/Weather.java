package com.bestcoolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zxit on 2018/4/16.
 */

public class Weather {
    public String status;
    public Basic basic;
    public Now now;
    @SerializedName("lifestyle")
    public List<LifestyleBean> Lifestyle;
    public Update update;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
