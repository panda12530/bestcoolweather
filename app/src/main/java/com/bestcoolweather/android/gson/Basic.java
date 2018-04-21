package com.bestcoolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zxit on 2018/4/16.
 */

public class Basic {
    @SerializedName("location")
    public String cityName;
    @SerializedName("cid")
    public String weatherId;

}
