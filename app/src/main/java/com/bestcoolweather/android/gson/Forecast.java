package com.bestcoolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zxit on 2018/4/16.
 */

public class Forecast {

    public String date;
    @SerializedName("tmp_max")
    public String max;
    @SerializedName("tmp_min")
    public String min;
    @SerializedName("cond_txt_d")
    public String dayinfo;
    @SerializedName("cond_txt_n")
    public String nightinfo;
}
