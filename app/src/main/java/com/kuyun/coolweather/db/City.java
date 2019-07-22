package com.kuyun.coolweather.db;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {
    public int id;
    public String cityName;
    public int cityCode;
    public int provinceId;
}
