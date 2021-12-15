package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;//对应天气的id
    public Update update;
    public class Update {
        @SerializedName("loc")
        public String updateTime;//天气的更新时间
    }
}
