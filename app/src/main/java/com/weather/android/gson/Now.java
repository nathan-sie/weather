package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Now {


    @SerializedName("tmp")
    public String temperature;//温度
    @SerializedName("cond")
    public More more;
    public class More {
        @SerializedName("txt")
        public String info;//晴,多云等...
    }


}
