package com.yjkmust.arcgisdemo.Option;

import com.google.gson.Gson;
import com.yjkmust.arcgisdemo.Bean.CityJsonBean;
import com.yjkmust.arcgisdemo.Bean.CityBean;
import com.yjkmust.arcgisdemo.MyApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GEOFLY on 2017/8/22.
 */

public class ParseJson {
    String strData = null;
    private static List<CityBean> cityList;
    private static List<List<CityJsonBean.StateBean.CityBean>> countryList;

    public ParseJson() {
        cityList = new ArrayList<>();
        countryList = new ArrayList<>();
        parseJson();
    }

    public List<CityBean> getCityList() {

        return cityList;
    }

    public List<List<CityJsonBean.StateBean.CityBean>> getCountryList() {

        return countryList;
    }

    public String getStrFromAssets(String name) {
        StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        try {
            inputStream = MyApp.getInstance().getAssets().open(name);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private void parseJson() {
        Gson gson = new Gson();
        CityJsonBean cityJsonBean = gson.fromJson(getStrFromAssets("yunnan.json"), CityJsonBean.class);
        List<CityJsonBean.StateBean> state = cityJsonBean.getState();
        for (CityJsonBean.StateBean data : state) {
            CityBean cityBean = new CityBean();
            cityBean.setCity(data.getName());
            cityBean.setCode(data.getCode());
            cityList.add(cityBean);
            countryList.add(data.getCity());
        }
    }
}
