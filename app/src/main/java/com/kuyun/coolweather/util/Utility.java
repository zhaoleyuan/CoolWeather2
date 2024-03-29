package com.kuyun.coolweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.kuyun.coolweather.db.City;
import com.kuyun.coolweather.db.County;
import com.kuyun.coolweather.db.Province;
import com.kuyun.coolweather.db.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for(int i=0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.provinceName = provinceObject.getString("name");
                    province.provinceCode = provinceObject.getInt("id");
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response,int provinceId) {
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for(int i=0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.cityName = cityObject.getString("name");
                    city.cityCode = cityObject.getInt("id");
                    city.provinceId = provinceId;
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response,int cityId) {
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for(int i=0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.countyName = countyObject.getString("name");
                    county.weatherId = countyObject.getString("weather_id");
                    county.cityId = cityId;
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }

    public static Weather handleWeatherResponse(String response) {
        return new Gson().fromJson(response,Weather.class);
    }
}











