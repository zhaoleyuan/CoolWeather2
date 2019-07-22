package com.kuyun.coolweather.db;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {
    public int id;
    public String countyName;
    public String weatherId;
    public String cityId;
}
