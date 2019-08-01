package com.kuyun.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kuyun.coolweather.db.City;
import com.kuyun.coolweather.db.County;
import com.kuyun.coolweather.db.Province;
import com.kuyun.coolweather.util.HttpUtil;
import com.kuyun.coolweather.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);
                    querycities();
                } else if(currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                } else if(currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(i).weatherId;
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == LEVEL_COUNTY) {
                    querycities();
                } else if(currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });

        queryProvinces();
    }

    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        if(provinceList.size() > 0) {
            dataList.clear();
            for(Province province : provinceList) {
                dataList.add((province.provinceName));
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String addr = "http://guolin.tech/api/china";
            queryFromServer(addr,LEVEL_PROVINCE);
        }
    }

    private void querycities() {
        titleText.setText(selectedProvince.provinceName);
        backButton.setVisibility(View.VISIBLE);

        cityList = LitePal.where("provinceId=?" , String.valueOf(selectedProvince.id)).find(City.class);
        if(cityList.size() > 0) {
            dataList.clear();
            for(City city : cityList) {
                dataList.add((city.cityName));
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.provinceCode;
            String addr = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(addr,LEVEL_CITY);
        }
    }

    private void queryCounties() {
        titleText.setText(selectedCity.cityName);
        backButton.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityId=?" , String.valueOf(selectedCity.id)).find(County.class);
        if(countyList.size() > 0) {
            dataList.clear();
            for(County county : countyList) {
                dataList.add((county.countyName));
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.provinceCode;
            int cityCode = selectedCity.cityCode;
            String addr = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(addr,LEVEL_COUNTY);
        }
    }

    private void queryFromServer(String addr, final int type) {
        showProgressDialog();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HttpUtil.sendOkHttpRequest(addr, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if(type == LEVEL_PROVINCE) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if(type == LEVEL_CITY) {
                    result = Utility.handleCityResponse(responseText,selectedProvince.id);
                } else if(type == LEVEL_COUNTY) {
                    result = Utility.handleCountyResponse(responseText,selectedCity.id);
                }
                if(result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if(type == LEVEL_PROVINCE) {
                                queryProvinces();
                            } else if(type == LEVEL_CITY) {
                                querycities();
                            } else if(type == LEVEL_COUNTY) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });

    }

    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}


























