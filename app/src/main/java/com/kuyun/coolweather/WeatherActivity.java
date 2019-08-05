package com.kuyun.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import butterknife.OnClick;
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
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_button)
    Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        if(Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ButterKnife.bind(this);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String bingPic = prefs.getString("bing_pic", null);
        if(bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }

        String weatherString = prefs.getString("weather", null);
        String weatherId;
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.HeWeather.get(0).basic.id;
            showWeatherInfo(weather);
        } else {
            weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        //drawerLayout.closeDrawers();
        //drawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.nav_button)
    public void openDrawer() {
        drawerLayout.openDrawer(Gravity.LEFT);
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
                        swipeRefresh.setRefreshing(false);
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
                        swipeRefresh.setRefreshing(false);
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


















