package com.kuyun.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kuyun.coolweather.db.Weather;
import com.kuyun.coolweather.util.HttpUtil;
import com.kuyun.coolweather.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "WeatherActivity";
    @BindView(R.id.title_city)
    TextView titleCity;
    @BindView(R.id.title_updata_time)
    TextView titleUpdataTime;
    @BindView(R.id.degree_text)
    TextView degreeText;
    @BindView(R.id.weather_info_text)
    TextView weatherInfoText;
    @BindView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @BindView(R.id.aqi_text)
    TextView aqiText;
    @BindView(R.id.pm25_text)
    TextView pm25Text;
    @BindView(R.id.comfort_text)
    TextView comfortText;
    @BindView(R.id.car_wash_text)
    TextView carWashText;
    @BindView(R.id.car_sport_text)
    TextView carSportText;
    @BindView(R.id.weather_layout)
    ScrollView weatherLayout;
    @BindView(R.id.bing_pic_img)
    ImageView bingPicImg;


//    private ScrollView weatherLayout;
//    private TextView titleCity;
//    private TextView titleUpdateTime;
//    private TextView degreeText;
//    private TextView weatherInfotext;
//    private LinearLayout forecastLayout;
//    private TextView aqiText;
//    private TextView pm25Text;
//    private TextView comfortText;
//    private TextView carWashText;
//    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String bingPic = prefs.getString("bing_pic", null);
        if(bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }

        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            String weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                edit.putString("bing_pic",bingPic).apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    public void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3974acl285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG,responseText);
                Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.HeWeather.get(0).status)) {
                            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            edit.putString("weather",responseText);
                            edit.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        loadBingPic();
    }

    private void showWeatherInfo(Weather weather) {
        Weather.HeWeatherBean heWeatherBean = weather.HeWeather.get(0);
        String cityName = heWeatherBean.basic.city;
        String updateTime = heWeatherBean.basic.update.loc.split(" ")[1];
        String degree = heWeatherBean.now.tmp + "C";
        String weatherInfo = heWeatherBean.now.cond_txt;

        titleCity.setText(cityName);
        titleUpdataTime.setText(updateTime);
        degreeText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        for (Weather.HeWeatherBean.DailyForecastBean forecast : heWeatherBean.daily_forecast) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);

            TextView dataText = view.findViewById(R.id.data_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);

            dataText.setText(forecast.date);
            infoText.setText(forecast.cond.txt_d);
            maxText.setText(forecast.tmp.max);
            minText.setText(forecast.tmp.min);
            forecastLayout.addView(view);
        }

        if(heWeatherBean.aqi != null) {
            aqiText.setText(heWeatherBean.aqi.city.aqi);
            pm25Text.setText(heWeatherBean.aqi.city.pm25);
        }
        String comfort = "舒适度: "+heWeatherBean.suggestion.comf.brf;
        String carwash = "洗车指数: "+heWeatherBean.suggestion.cw.brf;
        String sport = "运动建议: "+heWeatherBean.suggestion.sport.brf;
        comfortText.setText(comfort);
        carWashText.setText(carwash);
        carSportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}


















