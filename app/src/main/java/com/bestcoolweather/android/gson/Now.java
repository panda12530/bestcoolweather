package com.bestcoolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zxit on 2018/4/16.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond_txt")
    public String info;
}
